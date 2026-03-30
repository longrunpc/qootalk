package com.lrchan.qootalk.application.user.port.in;

import com.lrchan.qootalk.application.user.dto.command.LoginUserCommand;
import com.lrchan.qootalk.application.user.dto.result.LoginResult;

public interface LoginUserUseCase {
    LoginResult login(LoginUserCommand command);
}
