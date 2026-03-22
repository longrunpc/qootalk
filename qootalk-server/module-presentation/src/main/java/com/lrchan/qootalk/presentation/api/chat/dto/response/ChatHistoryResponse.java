package com.lrchan.qootalk.presentation.api.chat.dto.response;

import java.time.LocalDateTime;

import com.lrchan.qootalk.application.chat.dto.result.ChatHistoryQueryResult;
import com.lrchan.qootalk.domain.chat.message.MessageType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅 이력 응답")
public record ChatHistoryResponse(
    @Schema(description = "메시지 ID", example = "1001")
    Long id,
    @Schema(description = "채팅방 ID", example = "10")
    Long roomId,
    @Schema(description = "발신자 ID", example = "1")
    Long senderId,
    @Schema(description = "메시지 내용", example = "안녕하세요")
    String content,
    @Schema(description = "메시지 타입", example = "TEXT")
    MessageType messageType,
    @Schema(description = "전송 시각", example = "2026-03-12T11:20:00")
    LocalDateTime createdAt
) {
    public static ChatHistoryResponse of(ChatHistoryQueryResult result) {
        return new ChatHistoryResponse(
            result.id(),
            result.roomId(),
            result.senderId(),
            result.content(),
            result.messageType(),
            result.createdAt()
        );
    }
}
