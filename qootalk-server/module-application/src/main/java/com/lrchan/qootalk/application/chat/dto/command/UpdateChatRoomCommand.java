package com.lrchan.qootalk.application.chat.dto.command;

public record UpdateChatRoomCommand(
    Long requesterId,
    Long roomId,
    String roomName
) {
}
