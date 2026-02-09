package com.lrchan.qootalk.application.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lrchan.qootalk.application.user.dto.command.RegisterUserCommand;
import com.lrchan.qootalk.application.user.dto.result.UserQueryResult;
import com.lrchan.qootalk.application.user.port.in.RegisterUserUseCase;
import com.lrchan.qootalk.application.user.port.out.SaveUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;
import com.lrchan.qootalk.domain.user.vo.Password;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterUserService implements RegisterUserUseCase {
    
    private final SaveUserPort saveUserPort;
    private final LoadUserPort loadUserPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserQueryResult register(RegisterUserCommand command) {
        if (loadUserPort.findByEmail(command.email().value()).isPresent()) {
            throw new DomainException(UserErrorCode.USER_ALREADY_EXISTS);
        }
        
        String encodedPassword = passwordEncoder.encode(command.password().encryptedPassword());
        
        User user = User.create(command.email(), new Password(encodedPassword), command.name());
        
        User registeredUser = saveUserPort.save(user);

        return UserQueryResult.of(registeredUser);
    }
}
