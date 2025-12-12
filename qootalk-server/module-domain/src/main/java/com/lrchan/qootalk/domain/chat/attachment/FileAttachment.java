package com.lrchan.qootalk.domain.chat.attachment;

import com.lrchan.qootalk.domain.chat.vo.ContentType;
import com.lrchan.qootalk.domain.chat.vo.FileName;
import com.lrchan.qootalk.domain.chat.vo.FileSize;
import com.lrchan.qootalk.domain.common.BaseModel;


public class FileAttachment extends BaseModel {

    private Long messageId;
    private Long uploaderId;

    private FileName originalFileName;
    private FileName storedFileName;
    private ContentType contentType;
    private FileSize fileSize;

    private Path storagePath;
    private StorageType storageType;

    private FileType fileType;
    private FileMetadata metadata;
    private FileSecurity fileSecurity;

    private FileAttachment(Long id, Long messageId, Long uploaderId, FileName originalFileName, FileName storedFileName, ContentType contentType, FileSize fileSize, Path storagePath, StorageType storageType, FileType fileType, FileMetadata metadata, FileSecurity fileSecurity, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(id, createdAt, updatedAt, deletedAt);
        this.messageId = messageId;
        this.uploaderId = uploaderId;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.storagePath = storagePath;
        this.storageType = storageType;
        this.fileType = fileType;
        this.metadata = metadata;
        this.fileSecurity = fileSecurity;
    }

    public static FileAttachment create(Long messageId, Long uploaderId, FileName originalFileName, FileName storedFileName, ContentType contentType, FileSize fileSize, Path storagePath, StorageType storageType, FileType fileType, FileMetadata metadata, FileSecurity fileSecurity) {
        return new FileAttachment(null, messageId, uploaderId, originalFileName, storedFileName, contentType, fileSize, storagePath, storageType, fileType, metadata, fileSecurity, LocalDateTime.now(), LocalDateTime.now(), null);
    }

    public Long messageId() {
        return messageId;
    }

    public Long uploaderId() {
        return uploaderId;
    }
}