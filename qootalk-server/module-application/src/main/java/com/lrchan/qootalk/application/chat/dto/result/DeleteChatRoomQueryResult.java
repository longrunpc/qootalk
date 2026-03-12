package com.lrchan.qootalk.application.chat.dto.result;

import java.time.LocalDateTime;

public record DeleteChatRoomQueryResult(
    Long id,
    String roomName,
    LocalDateTime deletedAt
) {
}
