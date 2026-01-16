package com.lrchan.qootalk.infrastructure.persistence.chat.attachment;

import java.util.Objects;

import com.lrchan.qootalk.domain.chat.vo.StorageType;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class FileMetadataEmbeddable {

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "stored_file_name", nullable = false)
    private String storedFileName;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_type", nullable = false)
    private StorageType storageType;

    protected FileMetadataEmbeddable() {
    }

    public FileMetadataEmbeddable(
            String originalFileName,
            String storedFileName,
            String contentType,
            Long fileSize,
            String storagePath,
            StorageType storageType) {
        this.originalFileName = Objects.requireNonNull(originalFileName);
        this.storedFileName = Objects.requireNonNull(storedFileName);
        this.contentType = Objects.requireNonNull(contentType);
        this.fileSize = Objects.requireNonNull(fileSize);
        this.storagePath = Objects.requireNonNull(storagePath);
        this.storageType = Objects.requireNonNull(storageType);
    }

    public String originalFileName() {
        return originalFileName;
    }

    public String storedFileName() {
        return storedFileName;
    }

    public String contentType() {
        return contentType;
    }

    public Long fileSize() {
        return fileSize;
    }

    public String storagePath() {
        return storagePath;
    }

    public StorageType storageType() {
        return storageType;
    }
}
