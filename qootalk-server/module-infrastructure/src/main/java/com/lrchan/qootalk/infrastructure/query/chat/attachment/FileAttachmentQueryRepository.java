package com.lrchan.qootalk.infrastructure.query.chat.attachment;

import com.lrchan.qootalk.infrastructure.persistence.chat.attachment.FileAttachmentEntity;

import org.springframework.data.domain.Page;

import com.lrchan.qootalk.domain.chat.attachment.FileType;

public interface FileAttachmentQueryRepository {
    Page<FileAttachmentEntity> findPageByRoomIdAndUploaderIdAndFileType(Long roomId, Long uploaderId, FileType fileType, int page, int size);
}
