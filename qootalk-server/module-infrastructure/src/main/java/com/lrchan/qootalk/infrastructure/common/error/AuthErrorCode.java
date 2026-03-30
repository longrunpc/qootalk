package com.lrchan.qootalk.infrastructure.common.error;

import com.lrchan.qootalk.common.error.ErrorCode;

public enum AuthErrorCode implements ErrorCode {
    
    INVALID_JWT_SIGNATURE("AUTH_001", "잘못된 JWT 서명입니다.", 401),
    INVALID_JWT_EXPIRED("AUTH_002", "만료된 JWT 토큰입니다.", 401),
    INVALID_JWT_UNSUPPORTED("AUTH_003", "지원되지 않는 JWT 토큰입니다.", 401),
    INVALID_JWT_ILLEGAL_ARGUMENT("AUTH_004", "잘못된 JWT 토큰입니다.", 401),
    INVALID_JWT_MALFORMED("AUTH_005", "잘못된 JWT 형식입니다.", 401);

    private final String code;
    private final String message;
    private final int httpStatus;

    AuthErrorCode(String code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }
}
