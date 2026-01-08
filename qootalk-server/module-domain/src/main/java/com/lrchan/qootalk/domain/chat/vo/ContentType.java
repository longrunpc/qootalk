package com.lrchan.qootalk.domain.chat.vo;

import java.util.Set;
import java.util.regex.Pattern;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;

public final class ContentType {

    private static final Pattern MIME_TYPE_PATTERN =
            Pattern.compile("^[a-z]+/[a-z0-9.+-]+$");

    private static final Set<String> BLOCKED_TYPES = Set.of(
            "application/octet-stream",
            "application/x-msdownload",
            "application/x-sh",
            "application/java-archive"
    );

    private final String value;

    public ContentType(String value) {
        String normalized = normalize(value);
        validate(normalized);
        this.value = normalized;
    }

    public String value() {
        return value;
    }

    private String normalize(String value) {
        if (value == null) {
            throw new DomainException(ChatErrorCode.CHAT_FILE_METADATA_INVALID_CONTENT_TYPE);
        }
        return value.trim().toLowerCase();
    }

    private void validate(String value) {
        if (value.isBlank()) {
            throw new DomainException(ChatErrorCode.CHAT_FILE_METADATA_INVALID_CONTENT_TYPE);
        }

        if (!MIME_TYPE_PATTERN.matcher(value).matches()) {
            throw new DomainException(ChatErrorCode.CHAT_FILE_METADATA_INVALID_CONTENT_TYPE);
        }

        if (BLOCKED_TYPES.contains(value)) {
            throw new DomainException(ChatErrorCode.CHAT_FILE_METADATA_INVALID_CONTENT_TYPE);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentType that = (ContentType) o;
        return java.util.Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(value);
    }
}
