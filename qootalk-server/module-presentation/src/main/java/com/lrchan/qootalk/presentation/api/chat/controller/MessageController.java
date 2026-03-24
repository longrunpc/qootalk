package com.lrchan.qootalk.presentation.api.chat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lrchan.qootalk.application.chat.dto.result.SendMessageQueryResult;
import com.lrchan.qootalk.application.chat.dto.result.UpdateMessageQueryResult;
import com.lrchan.qootalk.application.chat.port.in.SendMessageUsecase;
import com.lrchan.qootalk.application.chat.port.in.UpdateMessageUsecase;
import com.lrchan.qootalk.common.response.ApiResponse;
import com.lrchan.qootalk.presentation.api.chat.dto.request.SendMessageRequest;
import com.lrchan.qootalk.presentation.api.chat.dto.request.UpdateMessageRequest;
import com.lrchan.qootalk.presentation.api.chat.dto.response.SendMessageResponse;
import com.lrchan.qootalk.presentation.api.chat.dto.response.UpdateMessageResponse;
import com.lrchan.qootalk.presentation.global.auth.AuthenticatedUserProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/chat-rooms/{roomId}/messages")
@RequiredArgsConstructor
@Tag(name = "Message", description = "메시지 API")
public class MessageController {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final SendMessageUsecase sendMessageUsecase;
    private final UpdateMessageUsecase updateMessageUsecase;

    @PostMapping
    @Operation(
        summary = "메시지 전송",
        description = "현재 로그인한 사용자가 채팅방에 메시지를 전송합니다."
    )
    public ResponseEntity<ApiResponse<SendMessageResponse>> sendMessage(
        @PathVariable Long roomId,
        @RequestBody SendMessageRequest request
    ) {
        Long requesterId = authenticatedUserProvider.getCurrentUserId();
        SendMessageQueryResult result = sendMessageUsecase.send(request.toCommand(requesterId, roomId));
        return ResponseEntity.ok(ApiResponse.of(SendMessageResponse.of(result)));
    }

    @PatchMapping("/{messageId}")
    @Operation(
        summary = "메시지 수정",
        description = "현재 로그인한 사용자가 본인이 작성한 메시지를 수정합니다."
    )
    public ResponseEntity<ApiResponse<UpdateMessageResponse>> updateMessage(
        @PathVariable Long roomId,
        @PathVariable Long messageId,
        @RequestBody UpdateMessageRequest request
    ) {
        Long requesterId = authenticatedUserProvider.getCurrentUserId();
        UpdateMessageQueryResult result = updateMessageUsecase.update(request.toCommand(requesterId, messageId));
        return ResponseEntity.ok(ApiResponse.of(UpdateMessageResponse.of(result)));
    }
}
