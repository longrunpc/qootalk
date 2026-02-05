package com.lrchan.qootalk.application.user.dto.command;

import com.lrchan.qootalk.domain.user.vo.Email;
import com.lrchan.qootalk.domain.user.vo.Password;
import com.lrchan.qootalk.domain.user.vo.UserName;

public record RegisterUserCommand(
    Email email,
    Password password,
    UserName name
) {
}
