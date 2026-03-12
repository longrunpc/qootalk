package com.lrchan.qootalk.infrastructure.persistence.chat.participant;

import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;

public class RoomParticipantEntityMapper {
    
    private RoomParticipantEntityMapper() {
    }

    public static RoomParticipantEntity toEntity(RoomParticipant roomParticipant) {
        return RoomParticipantEntity.builder()
            .id(roomParticipant.id())
            .userId(roomParticipant.userId())
            .roomId(roomParticipant.roomId())
            .lastReadMessageId(roomParticipant.lastReadMessageId())
            .role(roomParticipant.role())
            .notificationEnabled(roomParticipant.notificationEnabled())
            .createdAt(roomParticipant.createdAt())
            .updatedAt(roomParticipant.updatedAt())
            .deletedAt(roomParticipant.deletedAt())
            .build();
    }

    public static RoomParticipant toDomain(RoomParticipantEntity roomParticipantEntity) {
        return RoomParticipant.reconstruct(
            roomParticipantEntity.getId(),
            roomParticipantEntity.getUserId(),
            roomParticipantEntity.getRoomId(),
            roomParticipantEntity.getLastReadMessageId(),
            roomParticipantEntity.getRole(),
            roomParticipantEntity.isNotificationEnabled(),
            roomParticipantEntity.getCreatedAt(),
            roomParticipantEntity.getUpdatedAt(),
            roomParticipantEntity.getDeletedAt()
        );
    }
}
