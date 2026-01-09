package com.lrchan.qootalk.infrastructure.persistence.chat.message;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageJpaRepository extends JpaRepository<MessageEntity, Long> {
}
