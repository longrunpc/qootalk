package com.lrchan.qootalk.application.user.error;

import com.lrchan.qootalk.common.error.ErrorCode;

public enum UserApplicationErrorCode implements ErrorCode {
    LOGIN_FAILED("USER_001", "로그인에 실패했습니다."),
    USER_PROFILE_IMAGE_URL_MISMATCH("USER_002", "프로필 이미지 URL이 일치하지 않습니다.");

    private final String code;
    private final String message;

    UserApplicationErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
