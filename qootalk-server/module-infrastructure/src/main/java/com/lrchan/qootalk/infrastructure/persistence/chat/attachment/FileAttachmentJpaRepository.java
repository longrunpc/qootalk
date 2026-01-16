package com.lrchan.qootalk.infrastructure.persistence.chat.attachment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileAttachmentJpaRepository extends JpaRepository<FileAttachmentEntity, Long> {
}
