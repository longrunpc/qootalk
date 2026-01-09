package com.lrchan.qootalk.infrastructure.persistence.chat.message;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageJpaRepository extends JpaRepository<MessageEntity, Long> {
    Optional<MessageEntity> findById(Long id);
}
