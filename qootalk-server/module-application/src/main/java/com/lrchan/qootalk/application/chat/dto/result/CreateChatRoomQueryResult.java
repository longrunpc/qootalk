package com.lrchan.qootalk.application.chat.dto.result;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.chat.room.ChatRoom;
import com.lrchan.qootalk.domain.chat.room.RoomType;

public record CreateChatRoomQueryResult(
    Long id,
    String roomName,
    RoomType roomType,
    Long createdBy,
    int participantCount,
    LocalDateTime createdAt
) {
    public static CreateChatRoomQueryResult of(ChatRoom chatRoom, int participantCount) {
        return new CreateChatRoomQueryResult(
            chatRoom.id(),
            chatRoom.roomName(),
            chatRoom.roomType(),
            chatRoom.createdBy(),
            participantCount,
            chatRoom.createdAt()
        );
    }
}
