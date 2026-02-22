package com.lrchan.qootalk.domain.user.vo;

import java.time.Duration;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

public record Token(
    String token,
    long expiresIn
) {
    public Duration getDuration() {
        return Duration.ofMillis(expiresIn);
    }
    public Token {
        if(token == null || token.isBlank()) {
            throw new DomainException(UserErrorCode.USER_INVALID_ISSUED_TOKEN);
        }
    }
}
