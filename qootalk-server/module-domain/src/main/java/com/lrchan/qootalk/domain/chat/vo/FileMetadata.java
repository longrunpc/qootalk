package com.lrchan.qootalk.domain.chat.vo;

import java.util.Objects;

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
        this.originalFileName = Objects.requireNonNull(originalFileName, "originalFileName");
        this.storedFileName = Objects.requireNonNull(storedFileName, "storedFileName");
        this.contentType = Objects.requireNonNull(contentType, "contentType");
        this.fileSize = Objects.requireNonNull(fileSize, "fileSize");
        this.storagePath = Objects.requireNonNull(storagePath, "storagePath");
        this.storageType = Objects.requireNonNull(storageType, "storageType");

        validatePolicy();
    }

    private void validatePolicy() {
        // TEMP 스토리지는 tmp 경로만 허용
        if (storageType == StorageType.TEMP &&
                !storagePath.value().startsWith("system/tmp/")) {
            throw new IllegalArgumentException("TEMP storage must use tmp path");
        }

        // LOCAL 스토리지는 로컬 경로만 허용
        if (storageType == StorageType.LOCAL &&
                storagePath.value().startsWith("s3/")) {
            throw new IllegalArgumentException("LOCAL storage cannot use remote path");
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
