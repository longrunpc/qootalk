package com.lrchan.qootalk.presentation.api.user.dto.response;

import java.time.LocalDateTime;

import com.lrchan.qootalk.application.user.dto.result.UserQueryResult;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 응답 데이터")
public record UserSignupResponse(
    @Schema(description = "사용자 ID", example = "1")
    Long id,
    @Schema(description = "이메일", example = "user@qootalk.com")
    String email,
    @Schema(description = "이름", example = "홍길동")
    String name,
    @Schema(description = "프로필 이미지 URL", example = "https://cdn.qootalk.com/profiles/1.png", nullable = true)
    String profileImageUrl,
    @Schema(description = "상태 메시지", example = "")
    String statusMessage,
    @Schema(description = "권한", example = "USER")
    String role,
    @Schema(description = "생성 일시", example = "2026-03-12T10:00:00")
    LocalDateTime createdAt,
    @Schema(description = "수정 일시", example = "2026-03-12T10:00:00")
    LocalDateTime updatedAt,
    @Schema(description = "삭제 일시", example = "null", nullable = true)
    LocalDateTime deletedAt
) {
    public static UserSignupResponse of(UserQueryResult result) {
        return new UserSignupResponse(
            result.id(),
            result.email(),
            result.name(),
            result.profileImageUrl(),
            result.statusMessage(),
            result.role(),
            result.createdAt(),
            result.updatedAt(),
            result.deletedAt()
        );
    }
}
