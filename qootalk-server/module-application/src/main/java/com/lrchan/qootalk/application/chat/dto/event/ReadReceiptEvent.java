package com.lrchan.qootalk.application.chat.dto.event;

import java.time.LocalDateTime;

public record ReadReceiptEvent(
    Long roomId,
    Long readerId,
    Long lastReadMessageId,
    LocalDateTime updatedAt
) {
}
