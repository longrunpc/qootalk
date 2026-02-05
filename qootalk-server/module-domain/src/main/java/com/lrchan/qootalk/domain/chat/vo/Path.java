package com.lrchan.qootalk.domain.chat.vo;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;

public final class Path {
    private final String value;
    
    public Path(String value) {
        validate(value);
        this.value = value;
    }
    
    public String value() {
        return value;
    }
    
    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException(ChatErrorCode.CHAT_FILE_METADATA_INVALID_PATH);
        }
        if (value.length() < 2 || value.length() > 200) {
            throw new DomainException(ChatErrorCode.CHAT_FILE_METADATA_INVALID_PATH);
        }
        if (!value.matches("^[a-zA-Z0-9._%+/-]+$")) {
            throw new DomainException(ChatErrorCode.CHAT_FILE_METADATA_INVALID_PATH);
        }
        if (!value.endsWith("/")) {
            throw new DomainException(ChatErrorCode.CHAT_FILE_METADATA_INVALID_PATH);
        }
        if (value.contains("//")) {
            throw new DomainException(ChatErrorCode.CHAT_FILE_METADATA_INVALID_PATH);
        }
        if (value.contains("..")) {
            throw new DomainException(ChatErrorCode.CHAT_FILE_METADATA_INVALID_PATH);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return value.equals(path.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
