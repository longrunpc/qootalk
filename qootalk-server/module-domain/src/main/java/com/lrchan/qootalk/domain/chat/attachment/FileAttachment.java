package com.lrchan.qootalk.domain.chat.attachment;

import java.time.LocalDateTime;
import java.util.Objects;

import com.lrchan.qootalk.domain.chat.vo.FileMetadata;
import com.lrchan.qootalk.domain.chat.vo.FileSecurity;
import com.lrchan.qootalk.domain.common.BaseModel;

import lombok.Builder;

public class FileAttachment extends BaseModel {

    private final Long messageId;
    private final Long uploaderId;

    private final FileMetadata metadata;
    private final FileType fileType;
    private final FileSecurity fileSecurity;

    @Builder
    protected FileAttachment(
            Long id,
            Long messageId,
            Long uploaderId,
            FileMetadata metadata,
            FileType fileType,
            FileSecurity fileSecurity,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt) {
        super(id, createdAt, updatedAt, deletedAt);
        this.messageId = Objects.requireNonNull(messageId);
        this.uploaderId = Objects.requireNonNull(uploaderId);
        this.metadata = Objects.requireNonNull(metadata);
        this.fileType = fileType == null ? FileType.DOCUMENT : fileType;
        this.fileSecurity = fileSecurity == null ? FileSecurity.defaultPrivate() : fileSecurity;
    }

    public static FileAttachment create(
            Long messageId,
            Long uploaderId,
            FileMetadata metadata,
            FileType fileType,
            FileSecurity fileSecurity) {
        return FileAttachment.builder()
                .messageId(messageId)
                .uploaderId(uploaderId)
                .metadata(metadata)
                .fileType(fileType)
                .fileSecurity(fileSecurity)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // DB 복구 전용 메서드
    public static FileAttachment reconstruct(
            Long id,
            Long messageId,
            Long uploaderId,
            FileMetadata metadata,
            FileType fileType,
            FileSecurity fileSecurity,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt) {
        return FileAttachment.builder()
                .id(id)
                .messageId(messageId)
                .uploaderId(uploaderId)
                .metadata(metadata)
                .fileType(fileType)
                .fileSecurity(fileSecurity)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }

    public Long messageId() {
        return messageId;
    }

    public Long uploaderId() {
        return uploaderId;
    }

    public FileMetadata metadata() {
        return metadata;
    }

    public FileType fileType() {
        return fileType;
    }

    public FileSecurity fileSecurity() {
        return fileSecurity;
    }
}
