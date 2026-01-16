package com.lrchan.qootalk.infrastructure.persistence.chat.participant;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;
import com.lrchan.qootalk.domain.chat.participant.RoomParticipantRepository;

@Component
public class RoomParticipantRepositoryAdapter implements RoomParticipantRepository {
    
    private final RoomParticipantJpaRepository roomParticipantJpaRepository;

    public RoomParticipantRepositoryAdapter(RoomParticipantJpaRepository roomParticipantJpaRepository) {
        this.roomParticipantJpaRepository = roomParticipantJpaRepository;
    }

    @Override
    public Optional<RoomParticipant> findById(Long id) {
        return roomParticipantJpaRepository.findById(id).map(RoomParticipantEntityMapper::toDomain);
    }

    @Override
    public RoomParticipant save(RoomParticipant roomParticipant) {
        RoomParticipantEntity roomParticipantEntity = RoomParticipantEntityMapper.toEntity(roomParticipant);
        RoomParticipantEntity savedEntity = roomParticipantJpaRepository.save(roomParticipantEntity);
        return RoomParticipantEntityMapper.toDomain(savedEntity);
    }
}
