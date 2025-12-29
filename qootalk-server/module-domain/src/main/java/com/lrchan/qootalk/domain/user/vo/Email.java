package com.lrchan.qootalk.domain.user.vo;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

public class Email {
    
    private final String value;

    public Email(String value) {
        validate(value);
        this.value = value;
    }

    public String value() {
        return value;
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException(UserErrorCode.USER_INVALID_EMAIL);
        }
        if (!value.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new DomainException(UserErrorCode.USER_INVALID_EMAIL);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        Email email = (Email) o;
        return java.util.Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
