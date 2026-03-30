package com.lrchan.qootalk.infrastructure.common.error;

import com.lrchan.qootalk.common.error.ErrorCode;

public enum S3ErrorCode implements ErrorCode {
    
    S3_FILE_UPLOAD_FAILED("S3_001", "S3 파일 업로드 실패하였습니다.", 500),
    S3_FILE_DELETE_FAILED("S3_002", "S3 파일 삭제 실패하였습니다.", 500);

    private final String code;
    private final String message;
    private final int httpStatus;

    S3ErrorCode(String code, String message, int httpStatus) {
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
