package com.lrchan.qootalk.presentation.api.chat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.lrchan.qootalk.application.chat.dto.result.CreateChatRoomQueryResult;
import com.lrchan.qootalk.application.chat.dto.result.ChatRoomQueryResult;
import com.lrchan.qootalk.application.chat.dto.result.ChatRoomDetailQueryResult;
import com.lrchan.qootalk.application.chat.dto.result.UpdateChatRoomQueryResult;
import com.lrchan.qootalk.application.chat.port.in.CreateChatRoomUsecase;
import com.lrchan.qootalk.application.chat.port.in.LoadChatRoomDetailUsecase;
import com.lrchan.qootalk.application.chat.port.in.LoadChatRoomsUsecase;
import com.lrchan.qootalk.application.chat.port.in.UpdateChatRoomUsecase;
import com.lrchan.qootalk.common.response.ApiResponse;
import com.lrchan.qootalk.common.response.PagedResponse;
import com.lrchan.qootalk.presentation.api.chat.dto.request.CreateChatRoomRequest;
import com.lrchan.qootalk.presentation.api.chat.dto.request.UpdateChatRoomRequest;
import com.lrchan.qootalk.presentation.api.chat.dto.response.ChatRoomDetailResponse;
import com.lrchan.qootalk.presentation.api.chat.dto.response.CreateChatRoomResponse;
import com.lrchan.qootalk.presentation.api.chat.dto.response.ChatRoomSummaryResponse;
import com.lrchan.qootalk.presentation.api.chat.dto.response.UpdateChatRoomResponse;
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
    private final LoadChatRoomDetailUsecase loadChatRoomDetailUsecase;
    private final LoadChatRoomsUsecase loadChatRoomsUsecase;
    private final UpdateChatRoomUsecase updateChatRoomUsecase;

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

    @GetMapping
    @Operation(
        summary = "채팅방 목록 조회",
        description = "현재 로그인한 사용자가 참여 중인 채팅방 목록을 조회합니다."
    )
    public ResponseEntity<ApiResponse<PagedResponse<ChatRoomSummaryResponse>>> getChatRooms(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Long requesterId = authenticatedUserProvider.getCurrentUserId();
        PagedResponse<ChatRoomQueryResult> result = loadChatRoomsUsecase.load(
            new com.lrchan.qootalk.application.chat.dto.command.LoadChatRoomsCommand(requesterId, page, size)
        );
        PagedResponse<ChatRoomSummaryResponse> response = PagedResponse.of(
            result.content().stream().map(ChatRoomSummaryResponse::of).toList(),
            result.page(),
            result.size(),
            result.totalElements(),
            result.totalPages()
        );
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping("/{roomId}")
    @Operation(
        summary = "채팅방 상세 조회",
        description = "현재 로그인한 사용자가 참여 중인 채팅방 상세 정보를 조회합니다."
    )
    public ResponseEntity<ApiResponse<ChatRoomDetailResponse>> getChatRoomDetail(@PathVariable Long roomId) {
        Long requesterId = authenticatedUserProvider.getCurrentUserId();
        ChatRoomDetailQueryResult result = loadChatRoomDetailUsecase.load(
            new com.lrchan.qootalk.application.chat.dto.command.LoadChatRoomDetailCommand(requesterId, roomId)
        );
        return ResponseEntity.ok(ApiResponse.of(ChatRoomDetailResponse.of(result)));
    }

    @PatchMapping("/{roomId}")
    @Operation(
        summary = "채팅방 수정",
        description = "현재 로그인한 사용자가 참여 중인 채팅방 이름을 수정합니다."
    )
    public ResponseEntity<ApiResponse<UpdateChatRoomResponse>> updateChatRoom(
        @PathVariable Long roomId,
        @RequestBody UpdateChatRoomRequest request
    ) {
        Long requesterId = authenticatedUserProvider.getCurrentUserId();
        UpdateChatRoomQueryResult result = updateChatRoomUsecase.update(request.toCommand(requesterId, roomId));
        return ResponseEntity.ok(ApiResponse.of(UpdateChatRoomResponse.of(result)));
    }
}
