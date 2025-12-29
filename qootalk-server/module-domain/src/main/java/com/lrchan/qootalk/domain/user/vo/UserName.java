package com.lrchan.qootalk.domain.user.vo;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

public class UserName {
    
    private String value;

    public UserName(String value) {
        validate(value);
        this.value = value;
    }

    public String value() {
        return value;
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException(UserErrorCode.USER_INVALID_NAME);
        }
        if (value.length() < 2 || value.length() > 20) {
            throw new DomainException(UserErrorCode.USER_INVALID_NAME);
        }
    }
}
