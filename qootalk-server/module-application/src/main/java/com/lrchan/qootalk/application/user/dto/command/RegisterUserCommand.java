package com.lrchan.qootalk.application.user.dto;

public record RegisterUserCommand(
    String email,
    String password,
    String name
) {
    public RegisterUserCommand {
        validate(email, password, name);
    }

    private void validate(String email, String password, String name) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
    }
}
