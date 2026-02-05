package com.lrchan.qootalk.domain.user.vo;

import java.util.Objects;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

public final class UserName {
    
    private final String value;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserName)) return false;
        UserName userName = (UserName) o;
        return Objects.equals(value, userName.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
