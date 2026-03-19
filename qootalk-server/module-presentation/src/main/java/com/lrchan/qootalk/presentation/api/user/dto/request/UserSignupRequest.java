package com.lrchan.qootalk.presentation.api.user.dto.request;

import com.lrchan.qootalk.application.user.dto.command.RegisterUserCommand;
import com.lrchan.qootalk.domain.user.vo.Email;
import com.lrchan.qootalk.domain.user.vo.Password;
import com.lrchan.qootalk.domain.user.vo.UserName;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 요청")
public record UserSignupRequest(
    @Schema(description = "사용자 이메일", example = "user@qootalk.com")
    String email,
    @Schema(description = "비밀번호", example = "P@ssw0rd!")
    String password,
    @Schema(description = "사용자 이름", example = "홍길동")
    String name
) {
    public RegisterUserCommand toCommand() {
        return new RegisterUserCommand(new Email(email), new Password(password), new UserName(name));
    }
}
