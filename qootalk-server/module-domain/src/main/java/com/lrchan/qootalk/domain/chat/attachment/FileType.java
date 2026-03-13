package com.lrchan.qootalk.domain.chat.attachment;

import com.lrchan.qootalk.domain.chat.vo.ContentType;

public enum FileType {
    IMAGE,
    VIDEO,
    DOCUMENT,
    AUDIO,
    OTHER;

    public static FileType fromContentType(ContentType contentType) {
        if (contentType.value().startsWith("image/")) {
            return IMAGE;
        } else if (contentType.value().startsWith("video/")) {
            return VIDEO;
        } else if (contentType.value().startsWith("audio/")) {
            return AUDIO;
        } else if (contentType.value().startsWith("application/pdf") || contentType.value().startsWith("application/msword") || contentType.value().startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            return DOCUMENT;
        }
        return OTHER;
    }
}
