package com.lrchan.qootalk.application.chat.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lrchan.qootalk.application.chat.dto.command.ReadChatRoomsCommand;
import com.lrchan.qootalk.application.chat.dto.result.ChatRoomQueryResult;
import com.lrchan.qootalk.application.chat.port.in.LoadChatRoomsUsecase;
import com.lrchan.qootalk.application.chat.port.out.LoadChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.LoadMessagePort;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.message.Message;
import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;
import com.lrchan.qootalk.domain.chat.room.ChatRoom;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LoadChatRoomsService implements LoadChatRoomsUsecase {
    
    private final LoadChatRoomPort loadChatRoomPort;
    private final LoadRoomParticipantPort loadRoomParticipantPort;
    private final LoadMessagePort loadMessagePort;

    @Override
    public List<ChatRoomQueryResult> read(ReadChatRoomsCommand command) {
        List<ChatRoom> chatRooms = loadChatRoomPort.findAllByUserId(command.userId(), command.page(), command.size());
        return chatRooms.stream().map(chatRoom -> {
            RoomParticipant roomParticipant = loadRoomParticipantPort.findByUserIdAndRoomId(command.userId(), chatRoom.id())
                .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_ROOM_PARTICIPANT_NOT_FOUND));

            Message lastMessage = loadMessagePort.findById(roomParticipant.lastReadMessageId())
                .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_ROOM_PARTICIPANT_INVALID_LAST_READ_MESSAGE_ID));

            int unreadCount = (int) Math.min(999, loadMessagePort.countByRoomIdAndIdAfter(chatRoom.id(), roomParticipant.lastReadMessageId()));
            ChatRoomQueryResult result = ChatRoomQueryResult.of(chatRoom, lastMessage, unreadCount);
            return result;
        }).toList();
    }
}
