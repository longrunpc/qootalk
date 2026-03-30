package com.lrchan.qootalk.application.user.dto.command;

import com.lrchan.qootalk.domain.user.vo.Email;
import com.lrchan.qootalk.domain.user.vo.Password;

public record LoginUserCommand(
    Email email,
    Password password
) {
}
