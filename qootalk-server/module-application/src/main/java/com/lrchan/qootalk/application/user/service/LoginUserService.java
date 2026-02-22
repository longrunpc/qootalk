package com.lrchan.qootalk.application.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lrchan.qootalk.application.user.dto.command.LoginUserCommand;
import com.lrchan.qootalk.application.user.dto.result.LoginResult;
import com.lrchan.qootalk.application.user.dto.result.TokenResponse;
import com.lrchan.qootalk.application.user.dto.result.UserQueryResult;
import com.lrchan.qootalk.application.user.error.UserApplicationErrorCode;
import com.lrchan.qootalk.application.user.port.in.LoginUserUseCase;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.application.user.port.out.RefreshTokenPort;
import com.lrchan.qootalk.application.user.port.out.TokenProvider;
import com.lrchan.qootalk.common.exception.ApplicationException;
import com.lrchan.qootalk.domain.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginUserService implements LoginUserUseCase {
    
    private final LoadUserPort loadUserPort;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenPort refreshTokenPort;

    @Override
    public LoginResult login(LoginUserCommand command) {
        User user = loadUserPort.findByEmail(command.email().value())
            .orElse(null);

        if (user == null || !passwordEncoder.matches(command.password().encryptedPassword(), user.password().encryptedPassword())) {
            throw new ApplicationException(UserApplicationErrorCode.LOGIN_FAILED);
        }

        String accessToken = tokenProvider.createAccessToken(String.valueOf(user.id()), user.role().name());
        String refreshToken = tokenProvider.createRefreshToken(String.valueOf(user.id()));

        refreshTokenPort.save(String.valueOf(user.id()), refreshToken);

        return LoginResult.of(UserQueryResult.of(user), new TokenResponse(accessToken, refreshToken));
    }
}
