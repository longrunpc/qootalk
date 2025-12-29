package com.lrchan.qootalk.domain.chat.vo;

import java.util.Objects;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;

public final class FileMetadata {

    private final FileName originalFileName;
    private final FileName storedFileName;
    private final ContentType contentType;
    private final FileSize fileSize;
    private final Path storagePath;
    private final StorageType storageType;

    public FileMetadata(
            FileName originalFileName,
            FileName storedFileName,
            ContentType contentType,
            FileSize fileSize,
            Path storagePath,
            StorageType storageType
    ) {
        this.originalFileName = Objects.requireNonNull(originalFileName);
        this.storedFileName = Objects.requireNonNull(storedFileName);
        this.contentType = Objects.requireNonNull(contentType);
        this.fileSize = Objects.requireNonNull(fileSize);
        this.storagePath = Objects.requireNonNull(storagePath);
        this.storageType = Objects.requireNonNull(storageType);

        validatePolicy();
    }

    private void validatePolicy() {
        if (storageType == StorageType.TEMP &&
                !(storagePath.value().startsWith("system/tmp/") && storagePath.value().endsWith("/"))) {
            throw new DomainException(ChatErrorCode.CHAT_FILE_METADATA_INVALID_STORAGE_TYPE);
        }
        if (storageType == StorageType.LOCAL &&
                !storagePath.value().startsWith("uploads/")) {
            throw new DomainException(ChatErrorCode.CHAT_FILE_METADATA_INVALID_STORAGE_TYPE);
        }
        if (storageType == StorageType.S3 &&
                !storagePath.value().startsWith("s3/")) {
            throw new DomainException(ChatErrorCode.CHAT_FILE_METADATA_INVALID_STORAGE_TYPE);
        }
    }

    public FileName originalFileName() {
        return originalFileName;
    }

    public FileName storedFileName() {
        return storedFileName;
    }

    public ContentType contentType() {
        return contentType;
    }

    public FileSize fileSize() {
        return fileSize;
    }

    public Path storagePath() {
        return storagePath;
    }

    public StorageType storageType() {
        return storageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileMetadata that)) return false;
        return Objects.equals(originalFileName, that.originalFileName)
                && Objects.equals(storedFileName, that.storedFileName)
                && Objects.equals(contentType, that.contentType)
                && Objects.equals(fileSize, that.fileSize)
                && Objects.equals(storagePath, that.storagePath)
                && Objects.equals(storageType, that.storageType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                originalFileName,
                storedFileName,
                contentType,
                fileSize,
                storagePath,
                storageType
        );
    }
}
