package com.lrchan.qootalk.presentation.api.chat.dto.request;

import java.util.List;

import com.lrchan.qootalk.application.chat.dto.command.SendMessageCommand;
import com.lrchan.qootalk.domain.chat.message.MessageType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메시지 전송 요청")
public record SendMessageRequest(
    @Schema(description = "메시지 내용", example = "안녕하세요")
    String content,
    @Schema(description = "메시지 타입", example = "TEXT")
    MessageType messageType,
    @Schema(description = "멘션 사용자 ID 목록", example = "[12, 15]")
    List<Long> mentions,
    @Schema(description = "부모 메시지 ID", example = "1000")
    Long parentMessageId,
    @Schema(description = "첨부 파일 ID 목록", example = "[101, 102]")
    List<Long> attachmentIds
) {
    public SendMessageCommand toCommand(Long requesterId, Long roomId) {
        return new SendMessageCommand(
            requesterId,
            roomId,
            content,
            messageType,
            mentions,
            parentMessageId,
            attachmentIds
        );
    }
}
