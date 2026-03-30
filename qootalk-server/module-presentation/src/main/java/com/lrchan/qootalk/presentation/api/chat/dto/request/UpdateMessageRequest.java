package com.lrchan.qootalk.presentation.api.chat.dto.request;

import com.lrchan.qootalk.application.chat.dto.command.UpdateMessageCommand;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메시지 수정 요청")
public record UpdateMessageRequest(
    @Schema(description = "수정할 메시지 내용", example = "안녕하세요. 수정본입니다.")
    String content
) {
    public UpdateMessageCommand toCommand(Long requesterId, Long messageId) {
        return new UpdateMessageCommand(requesterId, messageId, content);
    }
}
