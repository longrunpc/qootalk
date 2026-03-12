package com.lrchan.qootalk.application.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lrchan.qootalk.application.chat.dto.command.LoadChatRoomDetailCommand;
import com.lrchan.qootalk.application.chat.dto.result.ChatRoomDetailQueryResult;
import com.lrchan.qootalk.application.chat.dto.result.ParticipantResult;
import com.lrchan.qootalk.application.chat.port.in.LoadChatRoomDetailUsecase;
import com.lrchan.qootalk.application.chat.port.out.LoadChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;
import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;
import com.lrchan.qootalk.domain.chat.room.ChatRoom;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoadChatRoomDetailService implements LoadChatRoomDetailUsecase {
    
    private final LoadUserPort loadUserPort;
    private final LoadChatRoomPort loadChatRoomPort;
    private final LoadRoomParticipantPort loadRoomParticipantPort;

    @Override
    public ChatRoomDetailQueryResult load(LoadChatRoomDetailCommand command) {
        // 유저 검증
        loadUserPort.findById(command.userId())
            .orElseThrow(() -> new DomainException(UserErrorCode.USER_NOT_FOUND));

        // 채팅방 조회
        ChatRoom chatRoom = loadChatRoomPort.findById(command.roomId())
            .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));

        // 채팅방 참여자 검증
        RoomParticipant roomParticipant = loadRoomParticipantPort.findByUserIdAndRoomId(command.userId(), command.roomId())
            .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_ROOM_PARTICIPANT_NOT_FOUND));

        List<ParticipantResult> participants = loadRoomParticipantPort.findByRoomId(command.roomId()).stream()
            .map(participant -> new ParticipantResult(participant.userId(), participant.role(), participant.createdAt()))
            .toList();

        return ChatRoomDetailQueryResult.of(chatRoom, participants, roomParticipant.notificationEnabled());
    }
}
