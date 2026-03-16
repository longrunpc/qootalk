package com.lrchan.qootalk.application.chat.port.out;

import org.springframework.data.domain.Page;

import com.lrchan.qootalk.domain.chat.attachment.FileAttachment;
import com.lrchan.qootalk.domain.chat.attachment.FileType;

public interface LoadFileAttachmentPort {
    Page<FileAttachment> findPageByRoomIdAndUploaderIdAndFileType(Long roomId, Long uploaderId, FileType fileType, int page, int size);
}
