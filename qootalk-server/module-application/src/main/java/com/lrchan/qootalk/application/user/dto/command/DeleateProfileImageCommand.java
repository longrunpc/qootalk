package com.lrchan.qootalk.application.user.dto.command;

import com.lrchan.qootalk.domain.user.vo.ProfileImageUrl;

public record DeleateProfileImageCommand(
    Long userId,
    ProfileImageUrl profileImageUrl
){
}
