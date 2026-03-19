package com.lrchan.qootalk.presentation.api.chat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lrchan.qootalk.application.chat.dto.result.CreateChatRoomQueryResult;
import com.lrchan.qootalk.application.chat.port.in.CreateChatRoomUsecase;
import com.lrchan.qootalk.common.response.ApiResponse;
import com.lrchan.qootalk.presentation.api.chat.dto.request.CreateChatRoomRequest;
import com.lrchan.qootalk.presentation.api.chat.dto.response.CreateChatRoomResponse;
import com.lrchan.qootalk.presentation.global.auth.AuthenticatedUserProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/chat-rooms")
@RequiredArgsConstructor
@Tag(name = "Chat Room", description = "채팅방 API")
public class ChatRoomController {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final CreateChatRoomUsecase createChatRoomUsecase;

    @PostMapping
    @Operation(
        summary = "채팅방 생성",
        description = "현재 로그인한 사용자가 새 채팅방을 생성합니다."
    )
    public ResponseEntity<ApiResponse<CreateChatRoomResponse>> createChatRoom(
        @RequestBody CreateChatRoomRequest request
    ) {
        Long requesterId = authenticatedUserProvider.getCurrentUserId();
        CreateChatRoomQueryResult result = createChatRoomUsecase.create(request.toCommand(requesterId));
        return ResponseEntity.ok(ApiResponse.of(CreateChatRoomResponse.of(result)));
    }
}
