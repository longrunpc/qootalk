package com.lrchan.qootalk.application.chat.port.out;

import java.util.Optional;

import com.lrchan.qootalk.domain.chat.room.ChatRoom;

public interface LoadChatRoomPort {
    Optional<ChatRoom> findById(Long id);
}
