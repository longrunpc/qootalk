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
import com.lrchan.qootalk.domain.user.vo.Token;

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
        // 유저 조회
        User user = loadUserPort.findByEmail(command.email().value())
            .orElseThrow(() -> new ApplicationException(UserApplicationErrorCode.LOGIN_FAILED));
        if (user.isDeleted()) {
            throw new ApplicationException(UserApplicationErrorCode.USER_DELETED);
        }

        // 비밀번호 검증
        if (user == null || !passwordEncoder.matches(command.password().encryptedPassword(), user.password().encryptedPassword())) {
            throw new ApplicationException(UserApplicationErrorCode.LOGIN_FAILED);
        }

        // 토큰 생성
        Token accessToken = tokenProvider.createAccessToken(String.valueOf(user.id()), user.role().name());
        Token refreshToken = tokenProvider.createRefreshToken(String.valueOf(user.id()));

        refreshTokenPort.save(String.valueOf(user.id()), refreshToken);

        return LoginResult.of(UserQueryResult.of(user), new TokenResponse(accessToken, refreshToken));
    }
}
