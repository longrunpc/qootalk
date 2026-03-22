package com.lrchan.qootalk.application.chat.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.lrchan.qootalk.application.chat.dto.command.SendMessageCommand;
import com.lrchan.qootalk.application.chat.dto.event.ChatMessageEvent;
import com.lrchan.qootalk.application.chat.dto.result.SendMessageQueryResult;
import com.lrchan.qootalk.application.chat.port.in.SendMessageUsecase;
import com.lrchan.qootalk.application.chat.port.out.LoadChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.LoadFileAttachmentPort;
import com.lrchan.qootalk.application.chat.port.out.LoadMessagePort;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.application.chat.port.out.PublishChatMessagePort;
import com.lrchan.qootalk.application.chat.port.out.SaveFileAttachmentPort;
import com.lrchan.qootalk.application.chat.port.out.SaveMessagePort;
import com.lrchan.qootalk.application.chat.port.out.SaveRoomParticipantPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.attachment.FileAttachment;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;
import com.lrchan.qootalk.domain.chat.message.Message;
import com.lrchan.qootalk.domain.chat.message.MessageType;
import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;
import com.lrchan.qootalk.domain.chat.room.ChatRoom;
import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SendMessageService implements SendMessageUsecase {

    private final LoadUserPort loadUserPort;
    private final LoadChatRoomPort loadChatRoomPort;
    private final LoadRoomParticipantPort loadRoomParticipantPort;
    private final LoadMessagePort loadMessagePort;
    private final LoadFileAttachmentPort loadFileAttachmentPort;
    private final SaveMessagePort saveMessagePort;
    private final SaveFileAttachmentPort saveFileAttachmentPort;
    private final SaveRoomParticipantPort saveRoomParticipantPort;
    private final PublishChatMessagePort publishChatMessagePort;

    @Override
    public SendMessageQueryResult send(SendMessageCommand command) {
        // 유저 검증
        User user = loadUserPort.findById(command.requesterId())
            .orElseThrow(() -> new DomainException(UserErrorCode.USER_NOT_FOUND));
        if (user.deletedAt() != null) {
            throw new DomainException(UserErrorCode.USER_DELETED);
        }

        // 채팅방 검증
        ChatRoom chatRoom = loadChatRoomPort.findById(command.roomId())
            .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));
        if (chatRoom.deletedAt() != null) {
            throw new DomainException(ChatErrorCode.CHAT_ROOM_DELETED);
        }

        // 채팅방 참여자 검증
        RoomParticipant participant = loadRoomParticipantPort.findByUserIdAndRoomId(command.requesterId(), command.roomId())
            .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_ROOM_PARTICIPANT_NOT_FOUND));
        if (participant.deletedAt() != null) {
            throw new DomainException(ChatErrorCode.CHAT_ROOM_PARTICIPANT_DELETED);
        }

        // 메시지 타입 검증
        MessageType messageType = command.messageType() == null ? MessageType.TEXT : command.messageType();
        validateMessagePayload(command, messageType);
        validateParentMessage(command.parentMessageId(), command.roomId());

        // 메시지 생성
        Message message = Message.createReply(
            command.roomId(),
            command.requesterId(),
            normalizeContent(command.content()),
            messageType,
            command.mentions(),
            command.parentMessageId()
        );
        Message savedMessage = saveMessagePort.save(message);

        // 첨부파일 연결
        List<Long> attachmentIds = bindAttachments(command, savedMessage.id());

        // 채팅방 참여자 업데이트
        participant.updateReadReceipt(savedMessage.id());
        saveRoomParticipantPort.save(participant);

        // 실시간 브로드캐스팅용 이벤트 발행
        publishChatMessagePort.publish(ChatMessageEvent.of(savedMessage, attachmentIds));

        return SendMessageQueryResult.of(savedMessage, attachmentIds);
    }

    private void validateMessagePayload(SendMessageCommand command, MessageType messageType) {
        boolean hasContent = StringUtils.hasText(command.content());
        boolean hasAttachments = command.attachmentIds() != null && !command.attachmentIds().isEmpty();

        if (!hasContent && !hasAttachments) {
            throw new DomainException(ChatErrorCode.CHAT_MESSAGE_EMPTY_PAYLOAD);
        }

        if (messageType == MessageType.TEXT && !hasContent) {
            throw new DomainException(ChatErrorCode.CHAT_MESSAGE_INVALID_CONTENT);
        }

        if ((messageType == MessageType.FILE || messageType == MessageType.IMAGE) && !hasAttachments) {
            throw new DomainException(ChatErrorCode.CHAT_MESSAGE_ATTACHMENT_REQUIRED);
        }
    }

    private void validateParentMessage(Long parentMessageId, Long roomId) {
        if (parentMessageId == null) {
            return;
        }

        Message parentMessage = loadMessagePort.findById(parentMessageId)
            .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_MESSAGE_NOT_FOUND));

        if (!roomId.equals(parentMessage.roomId())) {
            throw new DomainException(ChatErrorCode.CHAT_MESSAGE_INVALID_PARENT);
        }
    }

    private List<Long> bindAttachments(SendMessageCommand command, Long messageId) {
        if (command.attachmentIds() == null || command.attachmentIds().isEmpty()) {
            return List.of();
        }

        List<Long> attachmentIds = new ArrayList<>();
        for (Long attachmentId : command.attachmentIds()) {
            FileAttachment attachment = loadFileAttachmentPort.findById(attachmentId)
                .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_FILE_ATTACHMENT_NOT_FOUND));

            if (attachment.deletedAt() != null) {
                throw new DomainException(ChatErrorCode.CHAT_FILE_ATTACHMENT_DELETED);
            }
            if (!command.roomId().equals(attachment.roomId())) {
                throw new DomainException(ChatErrorCode.CHAT_MESSAGE_ATTACHMENT_ROOM_MISMATCH);
            }
            if (!command.requesterId().equals(attachment.uploaderId())) {
                throw new DomainException(ChatErrorCode.CHAT_MESSAGE_ATTACHMENT_OWNER_MISMATCH);
            }

            attachment.setMessageId(messageId);
            saveFileAttachmentPort.save(attachment);
            attachmentIds.add(attachment.id());
        }
        return List.copyOf(attachmentIds);
    }

    private String normalizeContent(String content) {
        return StringUtils.hasText(content) ? content.trim() : null;
    }
}
