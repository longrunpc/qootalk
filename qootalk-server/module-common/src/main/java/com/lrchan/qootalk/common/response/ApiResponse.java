package com.lrchan.qootalk.common.response;

import com.lrchan.qootalk.common.error.ErrorCode;

public record ApiResponse<T>(
    boolean success,
    T data,
    ErrorResponse error
) {
    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<Void> of(ErrorCode error) {
        return new ApiResponse<>(false, null, ErrorResponse.of(error));
    }

    public record ErrorResponse(
        String code,
        String message
    ) {
        public static ErrorResponse of(ErrorCode error) {
            return new ErrorResponse(error.getCode(), error.getMessage());
        }
    }
}
