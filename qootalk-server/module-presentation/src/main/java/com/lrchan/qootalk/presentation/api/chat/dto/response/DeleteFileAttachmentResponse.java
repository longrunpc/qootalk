package com.lrchan.qootalk.presentation.api.chat.dto.response;

import java.time.LocalDateTime;

import com.lrchan.qootalk.application.chat.dto.result.DeleteFileAttachmentQueryResult;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "파일 삭제 응답")
public record DeleteFileAttachmentResponse(
    @Schema(description = "파일 ID", example = "501")
    Long id,
    @Schema(description = "삭제 일시", example = "2026-03-12T11:45:00")
    LocalDateTime deletedAt
) {
    public static DeleteFileAttachmentResponse of(DeleteFileAttachmentQueryResult result) {
        return new DeleteFileAttachmentResponse(result.id(), result.deletedAt());
    }
}
