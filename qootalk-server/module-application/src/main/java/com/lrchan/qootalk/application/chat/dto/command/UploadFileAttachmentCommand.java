package com.lrchan.qootalk.application.chat.dto.command;

import java.io.InputStream;

public record UploadFileAttachmentCommand(
    Long requesterId,
    Long roomId,
    InputStream inputStream,
    String originalFileName,
    String contentType,
    long fileSize
) {
}
