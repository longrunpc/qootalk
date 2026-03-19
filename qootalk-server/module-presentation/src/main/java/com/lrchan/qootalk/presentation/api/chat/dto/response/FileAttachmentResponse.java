package com.lrchan.qootalk.presentation.api.chat.dto.response;

import java.time.LocalDateTime;

import com.lrchan.qootalk.application.chat.dto.result.FileAttachmentQueryResult;
import com.lrchan.qootalk.domain.chat.attachment.FileType;
import com.lrchan.qootalk.domain.chat.vo.StorageType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "파일 응답")
public record FileAttachmentResponse(
    @Schema(description = "파일 ID", example = "501")
    Long id,
    @Schema(description = "메시지 ID", example = "1001", nullable = true)
    Long messageId,
    @Schema(description = "업로더 사용자 ID", example = "1")
    Long uploaderId,
    @Schema(description = "원본 파일명", example = "architecture.pdf")
    String fileName,
    @Schema(description = "파일 타입", example = "DOCUMENT")
    FileType fileType,
    @Schema(description = "콘텐츠 타입", example = "application/pdf")
    String contentType,
    @Schema(description = "파일 크기(bytes)", example = "102400")
    Long fileSize,
    @Schema(description = "저장소 타입", example = "LOCAL")
    StorageType storageType,
    @Schema(description = "저장 경로", example = "uploads/chat/10/attachments/1001/")
    String storagePath,
    @Schema(description = "생성 일시", example = "2026-03-12T11:40:00")
    LocalDateTime createdAt
) {
    public static FileAttachmentResponse of(FileAttachmentQueryResult result) {
        return new FileAttachmentResponse(
            result.id(),
            result.messageId(),
            result.uploaderId(),
            result.fileName().value(),
            result.fileType(),
            result.contentType().value(),
            result.fileSize().value(),
            result.storageType(),
            result.storagePath().value(),
            result.createdAt()
        );
    }
}
