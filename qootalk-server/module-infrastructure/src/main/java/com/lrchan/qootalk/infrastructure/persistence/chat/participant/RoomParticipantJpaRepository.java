package com.lrchan.qootalk.infrastructure.persistence.chat.participant;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomParticipantJpaRepository extends JpaRepository<RoomParticipantEntity, Long> {
    Optional<RoomParticipantEntity> findByUserIdAndRoomId(Long userId, Long roomId);
    boolean existsByUserIdAndRoomId(Long userId, Long roomId);
    List<RoomParticipantEntity> findByRoomId(Long roomId);

    @Query("SELECT rp FROM RoomParticipantEntity rp " +
       "WHERE rp.roomId = :roomId AND rp.deletedAt IS NULL ")
    List<RoomParticipantEntity> findActiveByRoomId(@Param("roomId") Long roomId);
    
    @Query("SELECT rp FROM RoomParticipantEntity rp " +
       "WHERE rp.userId = :userId AND rp.deletedAt IS NULL " +
       "ORDER BY rp.lastReadMessageId DESC")
    Page<RoomParticipantEntity> findActivePageByUserId(@Param("userId") Long userId, Pageable pageable);
}
