package com.lrchan.qootalk.application.chat.dto.event;

public record UserChatMessageEvent(
    Long recipientId,
    ChatMessageEvent message
) {
}
