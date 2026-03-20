package com.lrchan.qootalk.application.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lrchan.qootalk.application.chat.dto.command.LoadChatRoomsCommand;
import com.lrchan.qootalk.application.chat.dto.result.ChatRoomQueryResult;
import com.lrchan.qootalk.application.chat.port.in.LoadChatRoomsUsecase;
import com.lrchan.qootalk.application.chat.port.out.LoadChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.LoadMessagePort;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.common.response.PagedResponse;
import com.lrchan.qootalk.domain.chat.message.Message;
import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;
import com.lrchan.qootalk.domain.chat.room.ChatRoom;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoadChatRoomsService implements LoadChatRoomsUsecase {
    
    private final LoadUserPort loadUserPort;
    private final LoadRoomParticipantPort loadRoomParticipantPort;
    private final LoadChatRoomPort loadChatRoomPort;
    private final LoadMessagePort loadMessagePort;

    @Override
    public PagedResponse<ChatRoomQueryResult> load(LoadChatRoomsCommand command) {
        // 유저 검증
        loadUserPort.findById(command.userId())
            .orElseThrow(() -> new DomainException(UserErrorCode.USER_NOT_FOUND));
        
        // 채팅방 리스트 조회
        PagedResponse<RoomParticipant> roomParticipants = loadRoomParticipantPort.findActivePageByUserId(command.userId(), command.page(), command.size());

        return PagedResponse.of(roomParticipants.content().stream().map(roomParticipant -> {
            ChatRoom chatRoom = loadChatRoomPort.findById(roomParticipant.roomId())
                .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));

            Message lastMessage = loadMessagePort.findById(roomParticipant.lastReadMessageId()).orElse(null);

            int unreadCount = (int) Math.min(999, loadMessagePort.countByRoomIdAndIdAfter(chatRoom.id(), roomParticipant.lastReadMessageId()));

            return ChatRoomQueryResult.of(chatRoom, lastMessage, unreadCount);
        }).toList(), roomParticipants.page(), roomParticipants.size(), roomParticipants.totalElements(), roomParticipants.totalPages());
    }
}
