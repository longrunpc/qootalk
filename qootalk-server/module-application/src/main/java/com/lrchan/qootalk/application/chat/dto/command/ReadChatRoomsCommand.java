package com.lrchan.qootalk.application.chat.dto.command;

public record ReadChatRoomsCommand(
    Long userId,
    int page,
    int size
) {
}
