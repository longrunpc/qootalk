package com.lrchan.qootalk.application.chat.dto.result;

public record DeleteMessageQueryResult(
    boolean deleted,
    Long messageId
) {
}
