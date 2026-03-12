package com.lrchan.qootalk.application.chat.port.out;

import java.util.List;

import com.lrchan.qootalk.domain.chat.room.ChatRoom;

public interface LoadChatRoomPort {
    List<ChatRoom> findAllByUserId(Long userId, int page, int size);
}
