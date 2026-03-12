package com.lrchan.qootalk.application.chat.dto.result;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.chat.room.ChatRoom;

public record DeleteChatRoomQueryResult(
    Long id,
    String roomName,
    LocalDateTime deletedAt
) {
    public static DeleteChatRoomQueryResult of(ChatRoom chatRoom) { 
        return new DeleteChatRoomQueryResult(
            chatRoom.id(),
            chatRoom.roomName(),
            chatRoom.deletedAt()
        );
    }
}
