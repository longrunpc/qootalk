package com.lrchan.qootalk.application.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lrchan.qootalk.application.chat.dto.command.CreateChatRoomCommand;
import com.lrchan.qootalk.application.chat.dto.result.CreateChatRoomQueryResult;
import com.lrchan.qootalk.application.chat.port.in.CreateChatRoomUsecase;
import com.lrchan.qootalk.application.chat.port.out.SaveChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.SaveMessagePort;
import com.lrchan.qootalk.application.chat.port.out.SaveRoomParticipantPort;
import com.lrchan.qootalk.domain.chat.message.Message;
import com.lrchan.qootalk.domain.chat.message.MessageType;
import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;
import com.lrchan.qootalk.domain.chat.participant.RoomRole;
import com.lrchan.qootalk.domain.chat.room.ChatRoom;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateChatRoomService implements CreateChatRoomUsecase {
    
    private final SaveChatRoomPort saveChatRoomPort;
    private final SaveRoomParticipantPort saveRoomParticipantPort;
    private final SaveMessagePort saveMessagePort;
    
    @Override
    public CreateChatRoomQueryResult create(CreateChatRoomCommand command) {
        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.create(command.roomName(), command.roomType(), command.requesterId());
        ChatRoom savedChatRoom = saveChatRoomPort.save(chatRoom);

        // 채팅방 생성 시스템 메세지 추가
        Message message = Message.create(savedChatRoom.id(), null, "채팅방을 생성했습니다.", MessageType.SYSTEM, null);
        Message savedMessage = saveMessagePort.save(message);

        // 채팅방 참여자 생성
        for (Long participantId : command.participantIds()) {
            RoomParticipant roomParticipant = RoomParticipant.create(
                participantId,
                savedChatRoom.id(),
                savedMessage.id(),
                RoomRole.MEMBER,
                command.notificationEnabled()
            );
            saveRoomParticipantPort.save(roomParticipant);
        }

        return CreateChatRoomQueryResult.of(savedChatRoom, command.participantIds().size());
    }
}
