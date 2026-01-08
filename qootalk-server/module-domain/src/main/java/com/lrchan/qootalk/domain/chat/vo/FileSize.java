package com.lrchan.qootalk.domain.chat.vo;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;

public final class FileSize {

    private static final long GLOBAL_MAX = 500L * 1024 * 1024;
    
    private final Long value;

    public FileSize(Long value) {
        validate(value);
        this.value = value;
    }
    
    public Long value() {
        return value;
    }

    private void validate(Long value) {
        if (value == null || value < 0) {
            throw new DomainException(ChatErrorCode.CHAT_FILE_METADATA_INVALID_FILE_SIZE);
        }
        if (value > GLOBAL_MAX) {
            throw new DomainException(ChatErrorCode.CHAT_FILE_METADATA_INVALID_FILE_SIZE);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileSize fileSize = (FileSize) o;
        return value.equals(fileSize.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
