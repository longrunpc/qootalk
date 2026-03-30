package com.lrchan.qootalk.application.chat.dto.result;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.chat.message.Message;

public record UpdateMessageQueryResult(
    Long messageId,
    String content,
    LocalDateTime updatedAt
) {
    public static UpdateMessageQueryResult of(Message message) {
        return new UpdateMessageQueryResult(
            message.id(),
            message.content(),
            message.updatedAt()
        );
    }
}
