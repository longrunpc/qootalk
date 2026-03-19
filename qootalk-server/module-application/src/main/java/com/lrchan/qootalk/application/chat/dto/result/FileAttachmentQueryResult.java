package com.lrchan.qootalk.application.chat.dto.result;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.chat.attachment.FileAttachment;
import com.lrchan.qootalk.domain.chat.attachment.FileType;
import com.lrchan.qootalk.domain.chat.vo.ContentType;
import com.lrchan.qootalk.domain.chat.vo.FileName;
import com.lrchan.qootalk.domain.chat.vo.FileSize;
import com.lrchan.qootalk.domain.chat.vo.Path;
import com.lrchan.qootalk.domain.chat.vo.StorageType;

import lombok.Builder;

@Builder
public record FileAttachmentQueryResult(
    Long id,
    Long messageId,
    Long uploaderId,
    FileName fileName,
    FileType fileType,
    ContentType contentType,
    FileSize fileSize,
    StorageType storageType,
    Path storagePath,
    LocalDateTime createdAt
) {
    public static FileAttachmentQueryResult of(FileAttachment fileAttachment) {
        return FileAttachmentQueryResult.builder()
            .id(fileAttachment.id())
            .messageId(fileAttachment.messageId())
            .uploaderId(fileAttachment.uploaderId())
            .fileName(fileAttachment.metadata().originalFileName())
            .fileType(fileAttachment.fileType())
            .contentType(fileAttachment.metadata().contentType())
            .fileSize(fileAttachment.metadata().fileSize())
            .storageType(fileAttachment.metadata().storageType())
            .storagePath(fileAttachment.metadata().storagePath())
            .createdAt(fileAttachment.createdAt())
            .build();
    }
}
