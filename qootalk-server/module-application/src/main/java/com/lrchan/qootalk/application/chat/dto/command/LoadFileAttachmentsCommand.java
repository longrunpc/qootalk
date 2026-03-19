package com.lrchan.qootalk.application.chat.dto.command;

import com.lrchan.qootalk.domain.chat.attachment.FileType;

public record LoadFileAttachmentsCommand(
    Long requesterId,
    Long roomId,
    Long uploaderId,
    FileType fileType,
    int page,
    int size
) {
}
