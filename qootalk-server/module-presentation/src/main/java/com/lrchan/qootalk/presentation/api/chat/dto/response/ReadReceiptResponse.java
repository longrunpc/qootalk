package com.lrchan.qootalk.presentation.api.chat.dto.response;

import com.lrchan.qootalk.application.chat.dto.result.ReadReceiptQueryResult;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메시지 읽음 처리 응답")
public record ReadReceiptResponse(
    @Schema(description = "채팅방 ID", example = "10")
    Long roomId,
    @Schema(description = "마지막 읽은 메시지 ID", example = "1001")
    Long lastReadMessageId,
    @Schema(description = "읽음 상태 갱신 여부", example = "true")
    boolean updated
) {
    public static ReadReceiptResponse of(ReadReceiptQueryResult result) {
        return new ReadReceiptResponse(
            result.roomId(),
            result.lastReadMessageId(),
            result.updated()
        );
    }
}
