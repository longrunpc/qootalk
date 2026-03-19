package com.lrchan.qootalk.presentation.api.user.dto.request;

import com.lrchan.qootalk.application.user.dto.command.DeleteProfileImageCommand;
import com.lrchan.qootalk.domain.user.vo.ProfileImageUrl;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로필 이미지 삭제 요청")
public record DeleteProfileImageRequest(
    @Schema(description = "삭제할 프로필 이미지 URL", example = "https://cdn.qootalk.com/profiles/1.png")
    String profileImageUrl
) {
    public DeleteProfileImageCommand toCommand(Long userId) {
        return new DeleteProfileImageCommand(userId, new ProfileImageUrl(profileImageUrl));
    }
}
