package com.lrchan.qootalk.infrastructure.persistence.chat.attachment;

import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.lrchan.qootalk.domain.chat.attachment.FileType;
import com.lrchan.qootalk.infrastructure.persistence.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "file_attachments")
@SQLDelete(sql = "UPDATE file_attachments SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class FileAttachmentEntity extends BaseEntity {
    
    @Column(name = "message_id", nullable = false)
    private Long messageId;
    
    @Column(name = "uploader_id", nullable = false)
    private Long uploaderId;
    
    @Embedded
    private FileMetadataEmbeddable metadata;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "file_type")
    private FileType fileType;
    
    @Embedded
    private FileSecurityEmbeddable security;

    protected FileAttachmentEntity() {
    }

    public FileAttachmentEntity(
            Long id,
            Long messageId,
            Long uploaderId,
            FileMetadataEmbeddable metadata,
            FileType fileType,
            FileSecurityEmbeddable security,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt) {
        this.id = id;
        this.messageId = Objects.requireNonNull(messageId);
        this.uploaderId = Objects.requireNonNull(uploaderId);
        this.metadata = Objects.requireNonNull(metadata);
        this.fileType = fileType == null ? FileType.DOCUMENT : fileType;
        this.security = Objects.requireNonNull(security);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public Long messageId() {
        return messageId;
    }

    public Long uploaderId() {
        return uploaderId;
    }

    public FileMetadataEmbeddable metadata() {
        return metadata;
    }

    public FileType fileType() {
        return fileType;
    }

    public FileSecurityEmbeddable security() {
        return security;
    }
}
