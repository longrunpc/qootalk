package com.lrchan.qootalk.infrastructure.persistence.chat.participant;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.application.chat.port.out.SaveRoomParticipantPort;
import com.lrchan.qootalk.common.response.PagedResponse;
import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;
import com.lrchan.qootalk.domain.chat.participant.RoomParticipantRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoomParticipantRepositoryAdapter implements RoomParticipantRepository, LoadRoomParticipantPort, SaveRoomParticipantPort {
    
    private final RoomParticipantJpaRepository roomParticipantJpaRepository;

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

    @Override
    public List<RoomParticipant> findByRoomId(Long roomId) {
        return roomParticipantJpaRepository.findByRoomId(roomId).stream().map(RoomParticipantEntityMapper::toDomain).toList();
    }

    @Override
    public Optional<RoomParticipant> findByUserIdAndRoomId(Long userId, Long roomId) {
        return roomParticipantJpaRepository.findByUserIdAndRoomId(userId, roomId).map(RoomParticipantEntityMapper::toDomain);
    }

    @Override
    public List<RoomParticipant> findActiveByRoomId(Long roomId) {
        return roomParticipantJpaRepository.findActiveByRoomId(roomId).stream().map(RoomParticipantEntityMapper::toDomain).toList();
    }
    
    @Override
    public PagedResponse<RoomParticipant> findActivePageByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoomParticipantEntity> roomParticipantEntities = roomParticipantJpaRepository.findActivePageByUserId(userId, pageable);

        return PagedResponse.of(roomParticipantEntities.getContent().stream().map(RoomParticipantEntityMapper::toDomain).toList(), roomParticipantEntities.getNumber(), roomParticipantEntities.getSize(), roomParticipantEntities.getTotalElements(), roomParticipantEntities.getTotalPages());
    }
}
