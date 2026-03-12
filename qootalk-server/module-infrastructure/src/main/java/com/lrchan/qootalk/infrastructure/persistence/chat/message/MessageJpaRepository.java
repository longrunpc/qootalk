package com.lrchan.qootalk.infrastructure.persistence.chat.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageJpaRepository extends JpaRepository<MessageEntity, Long> {
    @Query("SELECT COUNT(*) FROM MessageEntity WHERE roomId = :roomId AND id > :id")
    Long countByRoomIdAndIdAfter(Long roomId, Long id);
}
