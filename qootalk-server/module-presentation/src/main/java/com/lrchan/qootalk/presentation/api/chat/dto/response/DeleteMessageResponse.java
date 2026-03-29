package com.lrchan.qootalk.presentation.api.chat.dto.response;

import com.lrchan.qootalk.application.chat.dto.result.DeleteMessageQueryResult;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메시지 삭제 응답")
public record DeleteMessageResponse(
    @Schema(description = "삭제 여부", example = "true")
    boolean deleted,
    @Schema(description = "메시지 ID", example = "1001")
    Long messageId
) {
    public static DeleteMessageResponse of(DeleteMessageQueryResult result) {
        return new DeleteMessageResponse(result.deleted(), result.messageId());
    }
}
