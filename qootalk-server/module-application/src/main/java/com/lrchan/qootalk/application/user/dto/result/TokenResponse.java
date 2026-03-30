package com.lrchan.qootalk.application.user.dto.result;

import com.lrchan.qootalk.domain.user.vo.Token;

public record TokenResponse(
    Token accessToken,
    Token refreshToken
) {
    public static TokenResponse of(Token accessToken, Token refreshToken) {
        return new TokenResponse(accessToken, refreshToken);
    }
}