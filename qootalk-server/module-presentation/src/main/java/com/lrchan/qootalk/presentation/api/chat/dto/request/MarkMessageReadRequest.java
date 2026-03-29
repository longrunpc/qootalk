package com.lrchan.qootalk.presentation.api.chat.dto.request;

import com.lrchan.qootalk.application.chat.dto.command.MarkMessageReadCommand;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메시지 읽음 처리 요청")
public record MarkMessageReadRequest(
    @Schema(description = "마지막으로 읽은 메시지 ID", example = "1001")
    Long lastReadMessageId
) {
    public MarkMessageReadCommand toCommand(Long requesterId, Long roomId) {
        return new MarkMessageReadCommand(requesterId, roomId, lastReadMessageId);
    }
}
