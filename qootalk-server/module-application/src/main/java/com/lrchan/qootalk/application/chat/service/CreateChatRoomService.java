package com.lrchan.qootalk.application.chat.service;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lrchan.qootalk.application.chat.dto.command.CreateChatRoomCommand;
import com.lrchan.qootalk.application.chat.dto.result.CreateChatRoomQueryResult;
import com.lrchan.qootalk.application.chat.port.in.CreateChatRoomUsecase;
import com.lrchan.qootalk.application.chat.port.out.SaveChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.SaveMessagePort;
import com.lrchan.qootalk.application.chat.port.out.SaveRoomParticipantPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.message.Message;
import com.lrchan.qootalk.domain.chat.message.MessageType;
import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;
import com.lrchan.qootalk.domain.chat.participant.RoomRole;
import com.lrchan.qootalk.domain.chat.room.ChatRoom;
import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateChatRoomService implements CreateChatRoomUsecase {
    
    private final LoadUserPort loadUserPort;
    private final SaveChatRoomPort saveChatRoomPort;
    private final SaveRoomParticipantPort saveRoomParticipantPort;
    private final SaveMessagePort saveMessagePort;
    
    @Override
    public CreateChatRoomQueryResult create(CreateChatRoomCommand command) {
        // 유저 검증
        User user = loadUserPort.findById(command.requesterId())
            .orElseThrow(() -> new DomainException(UserErrorCode.USER_NOT_FOUND));
        if (user.deletedAt() != null) {
            throw new DomainException(UserErrorCode.USER_DELETED);
        }
        for (Long participantId : command.participantIds()) {
            User participant = loadUserPort.findById(participantId)
                .orElseThrow(() -> new DomainException(UserErrorCode.USER_NOT_FOUND));
            if (participant.deletedAt() != null) {
                throw new DomainException(UserErrorCode.USER_DELETED);
            }
        }
        
        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.create(command.roomName(), command.roomType(), command.requesterId());
        ChatRoom savedChatRoom = saveChatRoomPort.save(chatRoom);

        // 채팅방 생성 시스템 메세지 추가
        Message message = Message.create(
            savedChatRoom.id(),
            command.requesterId(),
            "채팅방을 생성했습니다.",
            MessageType.SYSTEM,
            null
        );
        Message savedMessage = saveMessagePort.save(message);

        // 생성자는 항상 OWNER 권한으로 참여한다.
        saveRoomParticipantPort.save(RoomParticipant.create(
            command.requesterId(),
            savedChatRoom.id(),
            savedMessage.id(),
            RoomRole.OWNER,
            command.notificationEnabled()
        ));

        Set<Long> participantIds = new LinkedHashSet<>(command.participantIds());
        participantIds.remove(command.requesterId());

        // 나머지 참여자는 MEMBER 권한으로 추가한다.
        for (Long participantId : participantIds) {
            RoomParticipant roomParticipant = RoomParticipant.create(
                participantId,
                savedChatRoom.id(),
                savedMessage.id(),
                RoomRole.MEMBER,
                command.notificationEnabled()
            );
            saveRoomParticipantPort.save(roomParticipant);
        }

        return CreateChatRoomQueryResult.of(savedChatRoom, participantIds.size() + 1);
    }
}
