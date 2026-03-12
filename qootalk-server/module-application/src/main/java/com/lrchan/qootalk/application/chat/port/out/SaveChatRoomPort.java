package com.lrchan.qootalk.application.chat.port.out;

import com.lrchan.qootalk.domain.chat.room.ChatRoom;

public interface SaveChatRoomPort {
    ChatRoom save(ChatRoom chatRoom);
}
