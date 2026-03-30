package com.lrchan.qootalk.application.chat.dto.result;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.chat.room.ChatRoom;

public record UpdateChatRoomQueryResult(
    Long id,
    String roomName,
    LocalDateTime updatedAt
) {
    public static UpdateChatRoomQueryResult of(ChatRoom chatRoom) {
        return new UpdateChatRoomQueryResult(
            chatRoom.id(),
            chatRoom.roomName(),
            chatRoom.updatedAt()
        );
    }
}
