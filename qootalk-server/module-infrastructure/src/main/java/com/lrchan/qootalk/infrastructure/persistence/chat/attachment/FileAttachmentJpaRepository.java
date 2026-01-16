package com.lrchan.qootalk.infrastructure.persistence.chat.attachment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileAttachmentJpaRepository extends JpaRepository<FileAttachmentEntity, Long> {
    Optional<FileAttachmentEntity> findByMessageId(Long messageId);
}
