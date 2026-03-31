package com.lrchan.qootalk.application.chat.dto.result;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.chat.message.Message;
import com.lrchan.qootalk.domain.chat.message.MessageType;

public record ChatHistoryQueryResult(
    Long id,
    Long roomId,
    Long senderId,
    String content,
    MessageType messageType,
    LocalDateTime createdAt
) {
    public static ChatHistoryQueryResult of(Message message) {
        return new ChatHistoryQueryResult(
            message.id(),
            message.roomId(),
            message.userId(),
            message.content(),
            message.messageType(),
            message.createdAt()
        );
    }
}
