package com.lrchan.qootalk.application.chat.port.out;

import com.lrchan.qootalk.domain.chat.attachment.FileAttachment;

public interface DeleteFileAttachmentPort {
    FileAttachment delete(FileAttachment fileAttachmentId);
}
