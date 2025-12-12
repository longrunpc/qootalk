package com.lrchan.qootalk.domain.chat.vo;

import java.util.Set;
import java.util.regex.Pattern;

public class ContentType {

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
            throw new IllegalArgumentException("Content type cannot be null");
        }
        return value.trim().toLowerCase();
    }

    private void validate(String value) {
        if (value.isBlank()) {
            throw new IllegalArgumentException("Content type cannot be blank");
        }

        if (!MIME_TYPE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid MIME type format: " + value);
        }

        if (BLOCKED_TYPES.contains(value)) {
            throw new IllegalArgumentException("Blocked content type: " + value);
        }
    }
}
