package com.lrchan.qootalk.application.chat.port.in;

import com.lrchan.qootalk.application.chat.dto.command.LoadFileAttachmentsCommand;
import com.lrchan.qootalk.application.chat.dto.result.FileAttachmentQueryResult;
import com.lrchan.qootalk.common.response.PagedResponse;

public interface LoadFileAttachmentsUsecase {
    PagedResponse<FileAttachmentQueryResult> load(LoadFileAttachmentsCommand command);
}
