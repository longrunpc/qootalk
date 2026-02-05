package com.lrchan.qootalk.application.user.dto.command;

public record RegisterUserCommand(
    String email,
    String password,
    String name
) {
}
