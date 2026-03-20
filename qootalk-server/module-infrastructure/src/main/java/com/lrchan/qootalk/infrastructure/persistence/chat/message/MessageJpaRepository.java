package com.lrchan.qootalk.infrastructure.persistence.chat.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageJpaRepository extends JpaRepository<MessageEntity, Long> {
    @Query("SELECT COUNT(*) FROM MessageEntity WHERE roomId = :roomId AND id > :id")
    Long countByRoomIdAndIdAfter(@Param("roomId") Long roomId, @Param("id") Long id);
}
