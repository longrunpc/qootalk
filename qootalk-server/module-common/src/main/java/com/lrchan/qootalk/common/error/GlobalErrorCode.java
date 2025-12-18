package com.lrchan.qootalk.common.error;

public enum GlobalErrorCode implements ErrorCode {

    INTERNAL_ERROR("GLOBAL_001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT("GLOBAL_002", "잘못된 요청입니다."),
    UNAUTHORIZED("GLOBAL_003", "인증되지 않은 요청입니다."),
    FORBIDDEN("GLOBAL_004", "권한이 없습니다.");

    private final String code;
    private final String message;

    GlobalErrorCode(String code, String message) {
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
