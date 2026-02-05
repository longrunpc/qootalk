package com.lrchan.qootalk.application.user.port.in;

import com.lrchan.qootalk.application.user.dto.command.RegisterUserCommand;
import com.lrchan.qootalk.application.user.dto.result.UserQueryResult;

public interface RegisterUserUseCase {
    UserQueryResult register(RegisterUserCommand command);
}
