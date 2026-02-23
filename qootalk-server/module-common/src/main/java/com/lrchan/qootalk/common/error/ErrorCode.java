package com.lrchan.qootalk.common.error;

public interface ErrorCode {
    String getCode();
    String getMessage();
    int getHttpStatus();
}
