package com.lrchan.qootalk.application.chat.dto.command;

public record DeleteFileAttachmentCommand(
    Long requesterId,
    Long roomId,
    Long fileAttachmentId
) {
}
