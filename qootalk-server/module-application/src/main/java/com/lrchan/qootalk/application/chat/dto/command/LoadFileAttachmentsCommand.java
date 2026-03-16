package com.lrchan.qootalk.application.chat.dto.command;

public record LoadFileAttachmentsCommand(
    Long requesterId,
    Long roomId,
    Long uploaderId,
    int page,
    int size
) {
}
