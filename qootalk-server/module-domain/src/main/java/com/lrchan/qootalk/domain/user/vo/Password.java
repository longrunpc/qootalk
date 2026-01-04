package com.lrchan.qootalk.domain.user.vo;

import java.util.Objects;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

public final class Password {
    
    private final String encryptedPassword;

    protected Password() {
        this.encryptedPassword = null;
    }

    public Password(String encryptedPassword) {
        validate(encryptedPassword);
        this.encryptedPassword = encryptedPassword;
    }

    public String encryptedPassword() {
        return encryptedPassword;
    }

    private void validate(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.isBlank()) {
            throw new DomainException(UserErrorCode.USER_INVALID_PASSWORD);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Password)) return false;
        Password password = (Password) o;
        return Objects.equals(encryptedPassword, password.encryptedPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(encryptedPassword);
    }
}
