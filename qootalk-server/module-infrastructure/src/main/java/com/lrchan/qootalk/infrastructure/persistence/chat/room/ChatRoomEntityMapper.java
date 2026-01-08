package com.lrchan.qootalk.infrastructure.persistence.chat.room;

import com.lrchan.qootalk.domain.chat.room.ChatRoom;
import com.lrchan.qootalk.domain.chat.vo.RoomName;

public final class ChatRoomEntityMapper {

    private ChatRoomEntityMapper() {
    }

    public static ChatRoomEntity toEntity(ChatRoom chatRoom) {
        return new ChatRoomEntity(chatRoom.roomName(), chatRoom.roomType(), chatRoom.createdBy());
    }

    public static ChatRoom toDomain(ChatRoomEntity chatRoomEntity) {
        return ChatRoom.reconstruct(chatRoomEntity.id(), new RoomName(chatRoomEntity.roomName()), chatRoomEntity.roomType(), chatRoomEntity.createdBy(), chatRoomEntity.createdAt(), chatRoomEntity.updatedAt(), chatRoomEntity.deletedAt());
    }
}
