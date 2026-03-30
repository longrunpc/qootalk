package com.lrchan.qootalk.application.chat.dto.command;

public record DeleteMessageCommand(
    Long requesterId,
    Long messageId
) {
}
