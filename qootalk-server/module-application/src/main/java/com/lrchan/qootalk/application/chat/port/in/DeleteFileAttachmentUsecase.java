package com.lrchan.qootalk.application.chat.port.in;

import com.lrchan.qootalk.application.chat.dto.command.DeleteFileAttachmentCommand;
import com.lrchan.qootalk.application.chat.dto.result.DeleteFileAttachmentQueryResult;

public interface DeleteFileAttachmentUsecase {
    DeleteFileAttachmentQueryResult delete(DeleteFileAttachmentCommand command);
}
