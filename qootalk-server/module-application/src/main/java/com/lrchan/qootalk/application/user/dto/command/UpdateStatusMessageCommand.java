package com.lrchan.qootalk.application.user.dto.command;

public record UpdateStatusMessageCommand(
    Long userId,
    String statusMessage
) {
    
}
