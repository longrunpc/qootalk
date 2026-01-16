package com.lrchan.qootalk.infrastructure.persistence.chat.participant;

import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;

public class RoomParticipantEntityMapper {
    
    private RoomParticipantEntityMapper() {
    }

    public static RoomParticipantEntity toEntity(RoomParticipant roomParticipant) {
        return new RoomParticipantEntity(roomParticipant.id(), roomParticipant.userId(), roomParticipant.roomId(), roomParticipant.lastReadMessageId(), roomParticipant.role(), roomParticipant.createdAt(), roomParticipant.updatedAt(), roomParticipant.deletedAt());
    }

    public static RoomParticipant toDomain(RoomParticipantEntity roomParticipantEntity) {
        return RoomParticipant.reconstruct(roomParticipantEntity.id(), roomParticipantEntity.userId(), roomParticipantEntity.roomId(), roomParticipantEntity.lastReadMessageId(), roomParticipantEntity.role(), roomParticipantEntity.createdAt(), roomParticipantEntity.updatedAt(), roomParticipantEntity.deletedAt());
    }
}
