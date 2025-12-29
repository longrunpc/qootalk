package com.lrchan.qootalk.domain.chat.attachment;

import java.time.LocalDateTime;
import java.util.Objects;

import com.lrchan.qootalk.domain.chat.vo.FileMetadata;
import com.lrchan.qootalk.domain.chat.vo.FileSecurity;
import com.lrchan.qootalk.domain.common.BaseModel;

public class FileAttachment extends BaseModel {

    private final Long messageId;
    private final Long uploaderId;

    private final FileMetadata metadata;
    private final FileType fileType;
    private final FileSecurity fileSecurity;

    private FileAttachment(
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
        this.metadata = metadata;
        this.fileType = fileType == null ? FileType.DOCUMENT : fileType;
        this.fileSecurity = fileSecurity == null ? FileSecurity.defaultPrivate() : fileSecurity;
    }

    public static FileAttachment create(
            Long messageId,
            Long uploaderId,
            FileMetadata metadata,
            FileType fileType,
            FileSecurity fileSecurity) {
        return new FileAttachment(
                null,
                messageId,
                uploaderId,
                metadata,
                fileType,
                fileSecurity,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null);
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
