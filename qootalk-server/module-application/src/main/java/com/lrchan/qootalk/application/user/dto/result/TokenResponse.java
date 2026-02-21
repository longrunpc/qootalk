package com.lrchan.qootalk.application.user.dto.result;

public record TokenResponse(
    String accessToken,
    String refreshToken
) {
}