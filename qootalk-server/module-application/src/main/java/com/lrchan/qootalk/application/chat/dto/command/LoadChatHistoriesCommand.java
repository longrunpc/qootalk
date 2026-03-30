package com.lrchan.qootalk.application.chat.dto.command;

public record LoadChatHistoriesCommand(
    Long requesterId,
    Long roomId,
    Long fromMessageId,
    int page,
    int size
) {
}
