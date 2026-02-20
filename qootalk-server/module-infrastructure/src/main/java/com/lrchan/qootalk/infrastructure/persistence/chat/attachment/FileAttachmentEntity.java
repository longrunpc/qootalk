package com.lrchan.qootalk.infrastructure.persistence.chat.attachment;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.lrchan.qootalk.domain.chat.attachment.FileType;
import com.lrchan.qootalk.infrastructure.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "file_attachments")
@SQLDelete(sql = "UPDATE file_attachments SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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
}
