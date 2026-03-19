package com.lrchan.qootalk.presentation.api.chat.dto.response;

import java.time.LocalDateTime;

import com.lrchan.qootalk.application.chat.dto.result.CreateChatRoomQueryResult;
import com.lrchan.qootalk.domain.chat.room.RoomType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅방 생성 응답")
public record CreateChatRoomResponse(
    @Schema(description = "채팅방 ID", example = "10")
    Long id,
    @Schema(description = "채팅방 이름", example = "백엔드 팀")
    String roomName,
    @Schema(description = "채팅방 타입", example = "GROUP")
    RoomType roomType,
    @Schema(description = "생성자 사용자 ID", example = "1")
    Long createdBy,
    @Schema(description = "참여자 수", example = "3")
    int participantCount,
    @Schema(description = "생성 일시", example = "2026-03-12T11:00:00")
    LocalDateTime createdAt
) {
    public static CreateChatRoomResponse of(CreateChatRoomQueryResult result) {
        return new CreateChatRoomResponse(
            result.id(),
            result.roomName(),
            result.roomType(),
            result.createdBy(),
            result.participantCount(),
            result.createdAt()
        );
    }
}
