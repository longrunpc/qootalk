package com.lrchan.qootalk.application.chat.dto.command;

public record UpdateMessageCommand(
    Long requesterId,
    Long messageId,
    String content
) {
}
