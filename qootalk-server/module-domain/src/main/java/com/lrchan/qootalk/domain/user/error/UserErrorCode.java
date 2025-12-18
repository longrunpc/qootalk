package com.lrchan.qootalk.domain.user.error;

import com.lrchan.qootalk.common.error.ErrorCode;

public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND("USER_001", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS("USER_002", "이미 존재하는 사용자입니다."),
    USER_INVALID_PASSWORD("USER_003", "비밀번호가 올바르지 않습니다."),
    USER_INVALID_EMAIL("USER_004", "이메일 형식이 올바르지 않습니다."),
    USER_INVALID_NAME("USER_005", "이름 형식이 올바르지 않습니다."),
    USER_INVALID_PROFILE_IMAGE_URL("USER_006", "프로필 이미지 URL 형식이 올바르지 않습니다."),
    USER_INVALID_STATUS_MESSAGE("USER_007", "상태 메시지 형식이 올바르지 않습니다."),
    USER_INVALID_ROLE("USER_008", "역할 형식이 올바르지 않습니다."),
    USER_DELETED("USER_009", "삭제된 사용자입니다.");

    private final String code;
    private final String message;

    UserErrorCode(String code, String message) {
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
