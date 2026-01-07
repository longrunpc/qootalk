package com.lrchan.qootalk.domain.user.vo;

import java.util.Objects;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

public final class StatusMessage {

    private static final int MAX_LENGTH = 100;
    
    private final String value;

    protected StatusMessage() {
        this.value = "";
    }

    public StatusMessage(String value) {
        value = value == null ? "" : value;
        validate(value);
        this.value = value;
    }

    public String value() {
        return value;
    }

    private void validate(String value) {
        if (value.length() > MAX_LENGTH) {
            throw new DomainException(UserErrorCode.USER_INVALID_STATUS_MESSAGE);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatusMessage)) return false;
        StatusMessage statusMessage = (StatusMessage) o;
        return Objects.equals(value, statusMessage.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
