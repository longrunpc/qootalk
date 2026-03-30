package com.lrchan.qootalk.application.chat.dto.result;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.chat.attachment.FileAttachment;

public record DeleteFileAttachmentQueryResult(
    Long id,
    LocalDateTime deletedAt
) {
    public static DeleteFileAttachmentQueryResult of(FileAttachment fileAttachment) {
        return new DeleteFileAttachmentQueryResult(
            fileAttachment.id(),
            fileAttachment.deletedAt()
        );
    }
}
