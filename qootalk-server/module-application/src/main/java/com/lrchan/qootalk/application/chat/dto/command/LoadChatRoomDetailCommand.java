package com.lrchan.qootalk.application.chat.dto.command;

public record LoadChatRoomDetailCommand(
    Long userId,
    Long roomId
) {
}
