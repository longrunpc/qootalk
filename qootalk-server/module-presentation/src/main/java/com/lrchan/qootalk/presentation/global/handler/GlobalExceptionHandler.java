package com.lrchan.qootalk.presentation.global.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.lrchan.qootalk.common.error.ErrorCode;
import com.lrchan.qootalk.common.error.GlobalErrorCode;
import com.lrchan.qootalk.common.exception.ApplicationException;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.common.exception.InfrastructureException;
import com.lrchan.qootalk.common.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomainException(DomainException e) {
        return errorResponse(e.getErrorCode());
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiResponse<Void>> handleApplicationException(ApplicationException e) {
        return errorResponse(e.getErrorCode());
    }

    @ExceptionHandler(InfrastructureException.class)
    public ResponseEntity<ApiResponse<Void>> handleInfrastructureException(InfrastructureException e) {
        return errorResponse(e.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        return errorResponse(GlobalErrorCode.INTERNAL_ERROR);
    }

    private ResponseEntity<ApiResponse<Void>> errorResponse(ErrorCode errorCode) {
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(ApiResponse.of(errorCode));
    }
}
