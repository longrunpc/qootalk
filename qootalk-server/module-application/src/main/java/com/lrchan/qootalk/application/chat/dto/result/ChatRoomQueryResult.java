package com.lrchan.qootalk.application.chat.dto.result;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.chat.room.ChatRoom;
import com.lrchan.qootalk.domain.chat.room.RoomType;

public record ChatRoomQueryResult(
    Long id,
    String roomName,
    RoomType roomType,
    Long createdBy,
    int participantCount,
    LocalDateTime createdAt
) {
    public static ChatRoomQueryResult of(ChatRoom chatRoom, int participantCount) {
        return new ChatRoomQueryResult(
            chatRoom.id(),
            chatRoom.roomName(),
            chatRoom.roomType(),
            chatRoom.createdBy(),
            participantCount,
            chatRoom.createdAt()
        );
    }
}
