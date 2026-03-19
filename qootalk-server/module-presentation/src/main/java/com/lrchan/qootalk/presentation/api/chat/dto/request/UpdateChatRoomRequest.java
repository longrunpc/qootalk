package com.lrchan.qootalk.presentation.api.chat.dto.request;

import com.lrchan.qootalk.application.chat.dto.command.UpdateChatRoomCommand;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅방 수정 요청")
public record UpdateChatRoomRequest(
    @Schema(description = "변경할 채팅방 이름", example = "백엔드 플랫폼 팀")
    String roomName
) {
    public UpdateChatRoomCommand toCommand(Long requesterId, Long roomId) {
        return new UpdateChatRoomCommand(requesterId, roomId, roomName);
    }
}
