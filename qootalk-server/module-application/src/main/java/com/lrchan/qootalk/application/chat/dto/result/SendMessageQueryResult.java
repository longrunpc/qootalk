package com.lrchan.qootalk.application.chat.dto.result;

import java.time.LocalDateTime;
import java.util.List;

import com.lrchan.qootalk.domain.chat.message.MessageType;

public record SendMessageQueryResult(
    Long id,
    Long roomId,
    Long senderId,
    String content,
    MessageType messageType,
    List<Long> mentions,
    Long parentMessageId,
    List<Long> attachmentIds,
    LocalDateTime createdAt
) {
}
