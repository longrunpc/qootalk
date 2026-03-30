package com.lrchan.qootalk.presentation.api.chat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lrchan.qootalk.application.chat.dto.command.LoadChatHistoriesCommand;
import com.lrchan.qootalk.application.chat.dto.result.ChatHistoryQueryResult;
import com.lrchan.qootalk.application.chat.dto.result.ReadReceiptQueryResult;
import com.lrchan.qootalk.application.chat.port.in.LoadChatHistoriesUsecase;
import com.lrchan.qootalk.application.chat.port.in.MarkMessageReadUsecase;
import com.lrchan.qootalk.common.response.ApiResponse;
import com.lrchan.qootalk.common.response.SliceResponse;
import com.lrchan.qootalk.presentation.api.chat.dto.request.MarkMessageReadRequest;
import com.lrchan.qootalk.presentation.api.chat.dto.response.ChatHistoryResponse;
import com.lrchan.qootalk.presentation.api.chat.dto.response.ReadReceiptResponse;
import com.lrchan.qootalk.presentation.global.auth.AuthenticatedUserProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/chat-rooms/{roomId}")
@RequiredArgsConstructor
@Tag(name = "Chat History", description = "채팅 이력 API")
public class ChatHistoryController {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final LoadChatHistoriesUsecase loadChatHistoriesUsecase;
    private final MarkMessageReadUsecase markMessageReadUsecase;

    @GetMapping("/histories")
    @Operation(
        summary = "채팅 이력 조회",
        description = "현재 로그인한 사용자가 참여 중인 채팅방의 메시지 이력을 Slice 형태로 조회합니다."
    )
    public ResponseEntity<ApiResponse<SliceResponse<ChatHistoryResponse>>> getChatHistories(
        @PathVariable Long roomId,
        @RequestParam(required = false) Long fromMessageId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size
    ) {
        Long requesterId = authenticatedUserProvider.getCurrentUserId();
        SliceResponse<ChatHistoryQueryResult> result = loadChatHistoriesUsecase.load(
            new LoadChatHistoriesCommand(requesterId, roomId, fromMessageId, page, size)
        );
        SliceResponse<ChatHistoryResponse> response = SliceResponse.of(
            result.content().stream().map(ChatHistoryResponse::of).toList(),
            result.page(),
            result.size(),
            result.hasNext()
        );
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PostMapping("/read")
    @Operation(
        summary = "메시지 읽음 처리",
        description = "현재 로그인한 사용자의 마지막 읽은 메시지 위치를 갱신합니다."
    )
    public ResponseEntity<ApiResponse<ReadReceiptResponse>> markAsRead(
        @PathVariable Long roomId,
        @RequestBody MarkMessageReadRequest request
    ) {
        Long requesterId = authenticatedUserProvider.getCurrentUserId();
        ReadReceiptQueryResult result = markMessageReadUsecase.mark(request.toCommand(requesterId, roomId));
        return ResponseEntity.ok(ApiResponse.of(ReadReceiptResponse.of(result)));
    }
}
