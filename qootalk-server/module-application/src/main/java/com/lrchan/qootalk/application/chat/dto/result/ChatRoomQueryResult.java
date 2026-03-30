package com.lrchan.qootalk.application.chat.dto.result;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.chat.message.Message;
import com.lrchan.qootalk.domain.chat.room.ChatRoom;
import com.lrchan.qootalk.domain.chat.room.RoomType;

public record ChatRoomQueryResult(
    Long id,
    String roomName,
    RoomType roomType,
    String lastMessage,
    int unreadCount,
    LocalDateTime updatedAt
) {
    public static ChatRoomQueryResult of(ChatRoom chatRoom, Message lastMessage, int unreadCount) {
        return new ChatRoomQueryResult(
            chatRoom.id(),
            chatRoom.roomName(),
            chatRoom.roomType(),
            lastMessage.content(),
            unreadCount,
            chatRoom.updatedAt()
        );
    }
}
