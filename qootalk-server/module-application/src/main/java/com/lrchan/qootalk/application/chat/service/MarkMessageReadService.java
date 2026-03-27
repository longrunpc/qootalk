package com.lrchan.qootalk.application.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lrchan.qootalk.application.chat.dto.command.MarkMessageReadCommand;
import com.lrchan.qootalk.application.chat.dto.event.ReadReceiptEvent;
import com.lrchan.qootalk.application.chat.dto.result.ReadReceiptQueryResult;
import com.lrchan.qootalk.application.chat.port.in.MarkMessageReadUsecase;
import com.lrchan.qootalk.application.chat.port.out.LoadChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.LoadMessagePort;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.application.chat.port.out.PublishReadReceiptPort;
import com.lrchan.qootalk.application.chat.port.out.SaveRoomParticipantPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;
import com.lrchan.qootalk.domain.chat.message.Message;
import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;
import com.lrchan.qootalk.domain.chat.room.ChatRoom;
import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MarkMessageReadService implements MarkMessageReadUsecase {

    private final LoadUserPort loadUserPort;
    private final LoadChatRoomPort loadChatRoomPort;
    private final LoadRoomParticipantPort loadRoomParticipantPort;
    private final LoadMessagePort loadMessagePort;
    private final SaveRoomParticipantPort saveRoomParticipantPort;
    private final PublishReadReceiptPort publishReadReceiptPort;

    @Override
    public ReadReceiptQueryResult mark(MarkMessageReadCommand command) {
        User user = loadUserPort.findById(command.requesterId())
            .orElseThrow(() -> new DomainException(UserErrorCode.USER_NOT_FOUND));
        if (user.deletedAt() != null) {
            throw new DomainException(UserErrorCode.USER_DELETED);
        }

        ChatRoom chatRoom = loadChatRoomPort.findById(command.roomId())
            .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));
        if (chatRoom.deletedAt() != null) {
            throw new DomainException(ChatErrorCode.CHAT_ROOM_DELETED);
        }

        RoomParticipant participant = loadRoomParticipantPort.findByUserIdAndRoomId(command.requesterId(), command.roomId())
            .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_ROOM_PARTICIPANT_NOT_FOUND));
        if (participant.deletedAt() != null) {
            throw new DomainException(ChatErrorCode.CHAT_ROOM_PARTICIPANT_DELETED);
        }

        Message message = loadMessagePort.findById(command.lastReadMessageId())
            .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_MESSAGE_NOT_FOUND));
        if (!command.roomId().equals(message.roomId())) {
            throw new DomainException(ChatErrorCode.CHAT_ROOM_PARTICIPANT_INVALID_LAST_READ_MESSAGE_ID);
        }

        if (command.lastReadMessageId() <= participant.lastReadMessageId()) {
            return new ReadReceiptQueryResult(command.roomId(), participant.lastReadMessageId(), false);
        }

        participant.updateReadReceipt(command.lastReadMessageId());
        saveRoomParticipantPort.save(participant);
        publishReadReceiptPort.publish(new ReadReceiptEvent(
            command.roomId(),
            command.requesterId(),
            participant.lastReadMessageId(),
            participant.updatedAt()
        ));
        return new ReadReceiptQueryResult(command.roomId(), participant.lastReadMessageId(), true);
    }
}
