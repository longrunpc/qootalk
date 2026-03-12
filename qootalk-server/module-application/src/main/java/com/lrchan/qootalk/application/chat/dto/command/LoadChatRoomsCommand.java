package com.lrchan.qootalk.application.chat.dto.command;

public record LoadChatRoomsCommand(
    Long userId,
    int page,
    int size
) {
}
