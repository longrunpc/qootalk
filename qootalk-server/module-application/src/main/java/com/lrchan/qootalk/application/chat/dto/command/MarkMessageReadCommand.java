package com.lrchan.qootalk.application.chat.dto.command;

public record MarkMessageReadCommand(
    Long requesterId,
    Long roomId,
    Long lastReadMessageId
) {
}
