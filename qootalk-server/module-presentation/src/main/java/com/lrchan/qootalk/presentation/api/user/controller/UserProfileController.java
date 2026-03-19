package com.lrchan.qootalk.presentation.api.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lrchan.qootalk.application.user.dto.result.UserQueryResult;
import com.lrchan.qootalk.application.user.port.in.UpdateStatusMessageUsecase;
import com.lrchan.qootalk.common.response.ApiResponse;
import com.lrchan.qootalk.presentation.api.user.dto.request.UpdateStatusMessageRequest;
import com.lrchan.qootalk.presentation.api.user.dto.response.UserProfileResponse;
import com.lrchan.qootalk.presentation.global.auth.AuthenticatedUserProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "사용자 프로필 API")
public class UserProfileController {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final UpdateStatusMessageUsecase updateStatusMessageUsecase;

    @PatchMapping("/status-message")
    @Operation(
        summary = "상태 메시지 수정",
        description = "현재 로그인한 사용자의 상태 메시지를 수정합니다."
    )
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateStatusMessage(
        @RequestBody UpdateStatusMessageRequest request
    ) {
        Long userId = authenticatedUserProvider.getCurrentUserId();
        UserQueryResult result = updateStatusMessageUsecase.update(request.toCommand(userId));
        return ResponseEntity.ok(ApiResponse.of(UserProfileResponse.of(result)));
    }
}
