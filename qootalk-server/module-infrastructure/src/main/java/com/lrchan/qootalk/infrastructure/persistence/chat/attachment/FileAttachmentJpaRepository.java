package com.lrchan.qootalk.infrastructure.persistence.chat.attachment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lrchan.qootalk.infrastructure.query.chat.attachment.FileAttachmentQueryRepository;

public interface FileAttachmentJpaRepository extends JpaRepository<FileAttachmentEntity, Long>, FileAttachmentQueryRepository {
    Optional<FileAttachmentEntity> findByMessageId(Long messageId);
}
