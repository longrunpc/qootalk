package com.lrchan.qootalk.presentation.api.chat.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.lrchan.qootalk.application.chat.dto.result.SendMessageQueryResult;
import com.lrchan.qootalk.domain.chat.message.MessageType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메시지 전송 응답")
public record SendMessageResponse(
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
    @Schema(description = "멘션 사용자 ID 목록", example = "[12, 15]")
    List<Long> mentions,
    @Schema(description = "부모 메시지 ID", example = "1000")
    Long parentMessageId,
    @Schema(description = "첨부 파일 ID 목록", example = "[101, 102]")
    List<Long> attachmentIds,
    @Schema(description = "생성 시각", example = "2026-03-12T11:20:00")
    LocalDateTime createdAt
) {
    public static SendMessageResponse of(SendMessageQueryResult result) {
        return new SendMessageResponse(
            result.id(),
            result.roomId(),
            result.senderId(),
            result.content(),
            result.messageType(),
            result.mentions(),
            result.parentMessageId(),
            result.attachmentIds(),
            result.createdAt()
        );
    }
}
