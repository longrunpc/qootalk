package com.lrchan.qootalk.application.chat.dto.event;

public record UserReadReceiptEvent(
    Long recipientId,
    ReadReceiptEvent readReceipt
) {
}
