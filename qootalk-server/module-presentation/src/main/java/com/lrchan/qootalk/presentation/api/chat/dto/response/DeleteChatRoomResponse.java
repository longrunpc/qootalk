package com.lrchan.qootalk.presentation.api.chat.dto.response;

import java.time.LocalDateTime;

import com.lrchan.qootalk.application.chat.dto.result.DeleteChatRoomQueryResult;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅방 삭제 응답")
public record DeleteChatRoomResponse(
    @Schema(description = "채팅방 ID", example = "10")
    Long id,
    @Schema(description = "삭제된 채팅방 이름", example = "백엔드 팀")
    String roomName,
    @Schema(description = "삭제 일시", example = "2026-03-12T11:15:00")
    LocalDateTime deletedAt
) {
    public static DeleteChatRoomResponse of(DeleteChatRoomQueryResult result) {
        return new DeleteChatRoomResponse(result.id(), result.roomName(), result.deletedAt());
    }
}
