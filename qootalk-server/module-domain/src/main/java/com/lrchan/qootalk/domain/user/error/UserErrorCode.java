package com.lrchan.qootalk.domain.user.error;

import com.lrchan.qootalk.common.error.ErrorCode;

public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND("USER_001", "사용자를 찾을 수 없습니다.", 404),
    USER_ALREADY_EXISTS("USER_002", "이미 존재하는 사용자입니다.", 400),
    USER_INVALID_PASSWORD("USER_003", "비밀번호가 올바르지 않습니다.", 400),
    USER_INVALID_EMAIL("USER_004", "이메일 형식이 올바르지 않습니다.", 400),
    USER_INVALID_NAME("USER_005", "이름 형식이 올바르지 않습니다.", 400),
    USER_INVALID_PROFILE_IMAGE_URL("USER_006", "프로필 이미지 URL 형식이 올바르지 않습니다.", 400),
    USER_INVALID_STATUS_MESSAGE("USER_007", "상태 메시지 형식이 올바르지 않습니다.", 400),
    USER_INVALID_ROLE("USER_008", "역할 형식이 올바르지 않습니다.", 400),
    USER_DELETED("USER_009", "삭제된 사용자입니다.", 400),
    USER_INVALID_ISSUED_TOKEN("USER_010", "발급된 토큰이 올바르지 않습니다.", 400),
    USER_INVALID_EXPIRED_TOKEN("USER_011", "만료된 토큰입니다.", 400);

    private final String code;
    private final String message;
    private final int httpStatus;

    UserErrorCode(String code, String message, int httpStatus) {
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
