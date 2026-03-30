package com.lrchan.qootalk.application.chat.port.out;

import com.lrchan.qootalk.application.chat.dto.event.ChatMessageEvent;

public interface PublishChatMessagePort {
    void publish(ChatMessageEvent event);
}
