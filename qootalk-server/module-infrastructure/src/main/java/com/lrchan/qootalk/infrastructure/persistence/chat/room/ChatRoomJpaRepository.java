package com.lrchan.qootalk.infrastructure.persistence.chat.room;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoomEntity, Long> {
    Optional<ChatRoomEntity> findByRoomName(String roomName);
    boolean existsByRoomName(String roomName);
}
