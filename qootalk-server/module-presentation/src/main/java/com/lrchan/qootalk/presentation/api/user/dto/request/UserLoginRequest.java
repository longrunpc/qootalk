package com.lrchan.qootalk.presentation.api.user.dto.request;

import com.lrchan.qootalk.application.user.dto.command.LoginUserCommand;
import com.lrchan.qootalk.domain.user.vo.Email;
import com.lrchan.qootalk.domain.user.vo.Password;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserLoginRequest(
    @Schema(description = "사용자 이메일", example = "user@qootalk.com")
    String email,
    @Schema(description = "비밀번호", example = "P@ssw0rd!")
    String password
) {
    public LoginUserCommand toCommand() {
        return new LoginUserCommand(new Email(email), new Password(password));
    }
}
