package com.lrchan.qootalk.application.user.dto.command;

import com.lrchan.qootalk.domain.user.vo.ProfileImageUrl;

public record DeleteProfileImageCommand(
    Long userId,
    ProfileImageUrl profileImageUrl
){
}
