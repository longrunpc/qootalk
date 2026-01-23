package com.lrchan.qootalk.infrastructure.persistence.chat.attachment;

import com.lrchan.qootalk.domain.chat.attachment.FileAttachment;
import com.lrchan.qootalk.domain.chat.vo.ContentType;
import com.lrchan.qootalk.domain.chat.vo.FileMetadata;
import com.lrchan.qootalk.domain.chat.vo.FileName;
import com.lrchan.qootalk.domain.chat.vo.FileSecurity;
import com.lrchan.qootalk.domain.chat.vo.FileSize;
import com.lrchan.qootalk.domain.chat.vo.Path;

public final class FileAttachmentMapper {

    private FileAttachmentMapper() {
    }

    public static FileAttachmentEntity toEntity(FileAttachment fileAttachment) {
        FileMetadata metadata = fileAttachment.metadata();
        FileSecurity security = fileAttachment.fileSecurity();

        FileMetadataEmbeddable metadataEmbeddable = new FileMetadataEmbeddable(
                metadata.originalFileName().value(),
                metadata.storedFileName().value(),
                metadata.contentType().value(),
                metadata.fileSize().value(),
                metadata.storagePath().value(),
                metadata.storageType()
        );

        FileSecurityEmbeddable securityEmbeddable = new FileSecurityEmbeddable(
                security.visibility(),
                security.downloadPolicy(),
                security.sharePolicy(),
                security.scanStatus(),
                security.encryption()
        );

        return FileAttachmentEntity.builder()
                .id(fileAttachment.id())
                .messageId(fileAttachment.messageId())
                .uploaderId(fileAttachment.uploaderId())
                .metadata(metadataEmbeddable)
                .fileType(fileAttachment.fileType())
                .security(securityEmbeddable)
                .createdAt(fileAttachment.createdAt())
                .updatedAt(fileAttachment.updatedAt())
                .deletedAt(fileAttachment.deletedAt())
                .build();
    }

    public static FileAttachment toDomain(FileAttachmentEntity entity) {
        FileMetadataEmbeddable metadataEmbeddable = entity.getMetadata();
        FileSecurityEmbeddable securityEmbeddable = entity.getSecurity();

        FileMetadata metadata = new FileMetadata(
                new FileName(metadataEmbeddable.originalFileName()),
                new FileName(metadataEmbeddable.storedFileName()),
                new ContentType(metadataEmbeddable.contentType()),
                new FileSize(metadataEmbeddable.fileSize()),
                new Path(metadataEmbeddable.storagePath()),
                metadataEmbeddable.storageType()
        );

        FileSecurity security = FileSecurity.reconstruct(
                securityEmbeddable.visibility(),
                securityEmbeddable.downloadPolicy(),
                securityEmbeddable.sharePolicy(),
                securityEmbeddable.scanStatus(),
                securityEmbeddable.encryption()
        );

        return FileAttachment.reconstruct(
                entity.getId(),
                entity.getMessageId(),
                entity.getUploaderId(),
                metadata,
                entity.getFileType(),
                security,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
