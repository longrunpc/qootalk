package com.lrchan.qootalk.presentation.api.chat.dto.response;

import java.time.LocalDateTime;

import com.lrchan.qootalk.application.chat.dto.result.UpdateMessageQueryResult;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메시지 수정 응답")
public record UpdateMessageResponse(
    @Schema(description = "메시지 ID", example = "1001")
    Long messageId,
    @Schema(description = "수정된 내용", example = "안녕하세요. 수정본입니다.")
    String content,
    @Schema(description = "수정 시각", example = "2026-03-12T11:21:00")
    LocalDateTime updatedAt
) {
    public static UpdateMessageResponse of(UpdateMessageQueryResult result) {
        return new UpdateMessageResponse(result.messageId(), result.content(), result.updatedAt());
    }
}
