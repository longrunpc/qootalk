package com.lrchan.qootalk.application.chat.dto.result;

import java.time.LocalDateTime;
import java.util.List;

import com.lrchan.qootalk.domain.chat.room.ChatRoom;
import com.lrchan.qootalk.domain.chat.room.RoomType;

public record ChatRoomDetailQueryResult(
    Long id,
    String roomName,
    RoomType roomType,
    Long createdBy,
    List<ParticipantResult> participants,
    boolean notificationEnabled,
    LocalDateTime createdAt
) {
    public static ChatRoomDetailQueryResult of(ChatRoom chatRoom, List<ParticipantResult> participants, boolean notificationEnabled) {
        return new ChatRoomDetailQueryResult(
            chatRoom.id(),
            chatRoom.roomName(),
            chatRoom.roomType(),
            chatRoom.createdBy(),
            participants,
            notificationEnabled,
            chatRoom.createdAt()
        );
    }
}
