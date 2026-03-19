package com.lrchan.qootalk.presentation.api.chat.dto.request;

import java.util.List;

import com.lrchan.qootalk.application.chat.dto.command.CreateChatRoomCommand;
import com.lrchan.qootalk.domain.chat.room.RoomType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅방 생성 요청")
public record CreateChatRoomRequest(
    @Schema(description = "채팅방 이름", example = "백엔드 팀")
    String roomName,
    @Schema(description = "채팅방 타입", example = "GROUP")
    RoomType roomType,
    @Schema(description = "참여자 사용자 ID 목록", example = "[1, 2, 3]")
    List<Long> participantIds,
    @Schema(description = "알림 활성화 여부", example = "true")
    boolean notificationEnabled
) {
    public CreateChatRoomCommand toCommand(Long requesterId) {
        return new CreateChatRoomCommand(requesterId, roomName, roomType, participantIds, notificationEnabled);
    }
}
