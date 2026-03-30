package com.lrchan.qootalk.application.chat.port.out;

import com.lrchan.qootalk.application.chat.dto.event.ReadReceiptEvent;

public interface PublishReadReceiptPort {
    void publish(ReadReceiptEvent event);
}
