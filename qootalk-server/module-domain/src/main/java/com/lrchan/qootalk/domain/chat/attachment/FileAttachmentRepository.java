package com.lrchan.qootalk.domain.chat.attachment;

import java.util.Optional;

public interface FileAttachmentRepository {
    Optional<FileAttachment> findById(Long id);
    Optional<FileAttachment> findByMessageId(Long messageId);
    FileAttachment save(FileAttachment fileAttachment);
}
