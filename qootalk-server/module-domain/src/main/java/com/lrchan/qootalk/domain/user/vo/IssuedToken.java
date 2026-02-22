package com.lrchan.qootalk.domain.user.vo;

import java.time.Duration;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

public record IssuedToken(
    String token,
    long expresIn
) {
    public Duration getDuration() {
        return Duration.ofMillis(expresIn);
    }
    public IssuedToken {
        if(token == null || token.isBlank()) {
            throw new DomainException(UserErrorCode.USER_INVALID_ISSUED_TOKEN);
        }
    }
}
