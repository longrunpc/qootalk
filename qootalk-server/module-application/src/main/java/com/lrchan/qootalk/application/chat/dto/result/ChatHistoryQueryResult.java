package com.lrchan.qootalk.application.chat.dto.result;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.chat.message.Message;
import com.lrchan.qootalk.domain.chat.message.MessageType;

import lombok.Builder;

@Builder
public record ChatHistoryQueryResult(
    Long id,
    Long roomId,
    Long senderId,
    String content,
    MessageType messageType,
    LocalDateTime createdAt
) {
    public static ChatHistoryQueryResult of(Message message) {
        return ChatHistoryQueryResult.builder()
            .id(message.id())
            .roomId(message.roomId())
            .senderId(message.userId())
            .content(message.content())
            .messageType(message.messageType())
            .createdAt(message.createdAt())
            .build();
    }
}
