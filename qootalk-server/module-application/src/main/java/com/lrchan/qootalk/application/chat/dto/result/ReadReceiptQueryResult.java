package com.lrchan.qootalk.application.chat.dto.result;

public record ReadReceiptQueryResult(
    Long roomId,
    Long lastReadMessageId,
    boolean updated
) {
}
