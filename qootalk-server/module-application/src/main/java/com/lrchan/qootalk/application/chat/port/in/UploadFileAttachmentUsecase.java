package com.lrchan.qootalk.application.chat.port.in;

import com.lrchan.qootalk.application.chat.dto.command.UploadFileAttachmentCommand;
import com.lrchan.qootalk.application.chat.dto.result.FileAttachmentQueryResult;

public interface UploadFileAttachmentUsecase {
    FileAttachmentQueryResult upload(UploadFileAttachmentCommand command);
}
