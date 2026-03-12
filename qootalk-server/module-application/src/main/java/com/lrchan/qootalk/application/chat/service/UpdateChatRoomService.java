package com.lrchan.qootalk.application.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lrchan.qootalk.application.chat.dto.command.UpdateChatRoomCommand;
import com.lrchan.qootalk.application.chat.dto.result.UpdateChatRoomQueryResult;
import com.lrchan.qootalk.application.chat.port.in.UpdateChatRoomUsecase;
import com.lrchan.qootalk.application.chat.port.out.LoadChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.application.chat.port.out.SaveChatRoomPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;
import com.lrchan.qootalk.domain.chat.room.ChatRoom;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateChatRoomService implements UpdateChatRoomUsecase {

    private final LoadUserPort loadUserPort;
    private final LoadChatRoomPort loadChatRoomPort;
    private final LoadRoomParticipantPort loadRoomParticipantPort;
    private final SaveChatRoomPort saveChatRoomPort;

    @Override
    public UpdateChatRoomQueryResult update(UpdateChatRoomCommand command) {
        // 유저 검증
        loadUserPort.findById(command.requesterId())
            .orElseThrow(() -> new DomainException(UserErrorCode.USER_NOT_FOUND));

        // 채팅방 검증
        ChatRoom chatRoom = loadChatRoomPort.findById(command.roomId())
            .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));

        // 채팅방 참여자 검증
        loadRoomParticipantPort.findByUserIdAndRoomId(command.requesterId(), command.roomId())
            .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_ROOM_PARTICIPANT_NOT_FOUND));

        // 채팅방 수정
        chatRoom.changeRoomName(command.roomName());
        ChatRoom updatedChatRoom = saveChatRoomPort.save(chatRoom);
        return UpdateChatRoomQueryResult.of(updatedChatRoom);
    }
    
}
