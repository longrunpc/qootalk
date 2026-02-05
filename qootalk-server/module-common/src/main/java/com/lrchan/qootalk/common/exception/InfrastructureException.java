package com.lrchan.qootalk.common.exception;

import com.lrchan.qootalk.common.error.ErrorCode;

public class InfrastructureException extends RuntimeException {
    
    private final ErrorCode errorCode;

    public InfrastructureException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public InfrastructureException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
