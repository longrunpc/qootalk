package com.lrchan.qootalk.infrastructure.persistence.chat.room;

import com.lrchan.qootalk.domain.chat.room.ChatRoom;
import com.lrchan.qootalk.domain.chat.vo.RoomName;

public final class ChatRoomEntityMapper {

    private ChatRoomEntityMapper() {
    }

    public static ChatRoomEntity toEntity(ChatRoom chatRoom) {
        return ChatRoomEntity.builder()
            .id(chatRoom.id())
            .roomName(chatRoom.roomName())
            .roomType(chatRoom.roomType())
            .createdBy(chatRoom.createdBy())
            .createdAt(chatRoom.createdAt())
            .updatedAt(chatRoom.updatedAt())
            .deletedAt(chatRoom.deletedAt())
            .build();
    }

    public static ChatRoom toDomain(ChatRoomEntity chatRoomEntity) {
        return ChatRoom.reconstruct(chatRoomEntity.getId(), new RoomName(chatRoomEntity.getRoomName()), chatRoomEntity.getRoomType(), chatRoomEntity.getCreatedBy(), chatRoomEntity.getCreatedAt(), chatRoomEntity.getUpdatedAt(), chatRoomEntity.getDeletedAt());
    }
}
