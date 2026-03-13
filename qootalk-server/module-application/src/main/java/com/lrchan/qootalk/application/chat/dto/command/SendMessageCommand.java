package com.lrchan.qootalk.application.chat.dto.command;

import java.util.List;

import com.lrchan.qootalk.domain.chat.message.MessageType;

public record SendMessageCommand(
    Long requesterId,
    Long roomId,
    String content,
    MessageType messageType,
    List<Long> mentions,
    Long parentMessageId,
    List<Long> attachmentIds
) {
}
