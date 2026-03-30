package com.lrchan.qootalk.application.chat.dto.command;

public record DeleteChatRoomCommand(
    Long requesterId,
    Long roomId
) {
}
