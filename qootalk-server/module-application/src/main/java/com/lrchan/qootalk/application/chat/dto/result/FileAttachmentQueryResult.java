package com.lrchan.qootalk.application.chat.dto.result;

import com.lrchan.qootalk.domain.chat.attachment.FileAttachment;

import lombok.Builder;

@Builder
public record FileAttachmentQueryResult(
    Long id,
    Long messageId,
    Long uploaderId,
    String fileName,
    String contentType,
    long fileSize
) {
    public static FileAttachmentQueryResult of(FileAttachment fileAttachment) {
        return new FileAttachmentQueryResult(
            fileAttachment.id(),
            fileAttachment.messageId(),
            fileAttachment.uploaderId(),
            fileAttachment.metadata().originalFileName().value(),
            fileAttachment.metadata().contentType().value(),
            fileAttachment.metadata().fileSize().value()
        );
    }
}
