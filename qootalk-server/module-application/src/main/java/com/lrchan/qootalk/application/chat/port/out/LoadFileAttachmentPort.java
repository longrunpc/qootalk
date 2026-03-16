package com.lrchan.qootalk.application.chat.port.out;

import com.lrchan.qootalk.common.response.PagedResponse;
import com.lrchan.qootalk.domain.chat.attachment.FileAttachment;
import com.lrchan.qootalk.domain.chat.attachment.FileType;

public interface LoadFileAttachmentPort {
    PagedResponse<FileAttachment> findPageByRoomIdAndUploaderIdAndFileType(Long roomId, Long uploaderId, FileType fileType, int page, int size);
}
