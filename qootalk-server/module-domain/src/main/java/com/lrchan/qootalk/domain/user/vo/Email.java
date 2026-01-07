package com.lrchan.qootalk.domain.user.vo;

import java.util.Objects;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

public final class Email {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    
    private String value;

    protected Email() {
    }

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
        if (!value.matches(EMAIL_REGEX)) {
            throw new DomainException(UserErrorCode.USER_INVALID_EMAIL);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
