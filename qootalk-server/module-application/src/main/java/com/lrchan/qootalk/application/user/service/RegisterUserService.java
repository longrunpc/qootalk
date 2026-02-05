package com.lrchan.qootalk.application.user.service;

import org.springframework.stereotype.Service;

import com.lrchan.qootalk.application.user.dto.command.RegisterUserCommand;
import com.lrchan.qootalk.application.user.dto.result.UserQueryResult;
import com.lrchan.qootalk.application.user.port.in.RegisterUserUseCase;
import com.lrchan.qootalk.application.user.port.out.SaveUserPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {
    
    private final SaveUserPort SaveUserPort;
    private final LoadUserPort LoadUserPort;

    @Override
    public UserQueryResult register(RegisterUserCommand command) {
        return null;
    }
}
