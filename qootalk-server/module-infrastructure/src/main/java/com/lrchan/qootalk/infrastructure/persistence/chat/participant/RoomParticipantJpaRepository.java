package com.lrchan.qootalk.infrastructure.persistence.chat.participant;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomParticipantJpaRepository extends JpaRepository<RoomParticipantEntity, Long> {
    Optional<RoomParticipantEntity> findByUserIdAndRoomId(Long userId, Long roomId);
    boolean existsByUserIdAndRoomId(Long userId, Long roomId);
    List<RoomParticipantEntity> findByRoomId(Long roomId);
}
