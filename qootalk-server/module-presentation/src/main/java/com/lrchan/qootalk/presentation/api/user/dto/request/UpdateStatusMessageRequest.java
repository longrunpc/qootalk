package com.lrchan.qootalk.presentation.api.user.dto.request;

import com.lrchan.qootalk.application.user.dto.command.UpdateStatusMessageCommand;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상태 메시지 수정 요청")
public record UpdateStatusMessageRequest(
    @Schema(description = "변경할 상태 메시지", example = "업무 집중")
    String statusMessage
) {
    public UpdateStatusMessageCommand toCommand(Long userId) {
        return new UpdateStatusMessageCommand(userId, statusMessage);
    }
}
