package com.lrchan.qootalk.application.user.service;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lrchan.qootalk.application.user.dto.command.LoginUserCommand;
import com.lrchan.qootalk.application.user.dto.result.LoginResult;
import com.lrchan.qootalk.application.user.error.UserApplicationErrorCode;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.application.user.port.out.TokenProvider;
import com.lrchan.qootalk.common.exception.ApplicationException;
import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.vo.Email;
import com.lrchan.qootalk.domain.user.vo.Password;
import com.lrchan.qootalk.domain.user.vo.UserName;

import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginUserService 테스트")
public class LoginUserServiceTest {
    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private LoginUserService loginUserService;
    
    @Test
    @DisplayName("로그인 성공")
    public void should_LoginSuccess() {
        // given
        LoginUserCommand command = new LoginUserCommand(
            new Email("test@example.com"),
            new Password("password123")
        );

        User user = User.create(command.email(), command.password(), new UserName("홍길동"));

        given(loadUserPort.findByEmail(command.email().value())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(command.password().encryptedPassword(), user.password().encryptedPassword())).willReturn(true);
        given(tokenProvider.createAccessToken(String.valueOf(user.id()), user.role().name())).willReturn("accessToken");
        given(tokenProvider.createRefreshToken(String.valueOf(user.id()))).willReturn("refreshToken");
        
        // when
        LoginResult result = loginUserService.login(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.user().email()).isEqualTo(command.email().value());
        assertThat(result.user().name()).isEqualTo(user.name().value());
        assertThat(result.token().accessToken()).isEqualTo("accessToken");
        assertThat(result.token().refreshToken()).isEqualTo("refreshToken");

        verify(loadUserPort, times(1)).findByEmail(command.email().value());
        verify(passwordEncoder, times(1)).matches(command.password().encryptedPassword(), user.password().encryptedPassword());
        verify(tokenProvider, times(1)).createAccessToken(String.valueOf(user.id()), user.role().name());
        verify(tokenProvider, times(1)).createRefreshToken(String.valueOf(user.id()));
    }

    @Test
    @DisplayName("로그인 실패: 이메일이 존재하지 않는 경우")
    public void should_LoginFailed_When_EmailNotFound() {
        // given
        LoginUserCommand command = new LoginUserCommand(
            new Email("test@example.com"),
            new Password("password123")
        );

        given(loadUserPort.findByEmail(command.email().value())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> loginUserService.login(command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(UserApplicationErrorCode.LOGIN_FAILED.getMessage());

        verify(loadUserPort, times(1)).findByEmail(command.email().value());
        verify(passwordEncoder, times(0)).encode(any(String.class));
    }

    @Test
    @DisplayName("로그인 실패: 비밀번호가 일치하지 않는 경우")
    public void should_LoginFailed_When_PasswordIsNotMatched() {
        // given
        LoginUserCommand command = new LoginUserCommand(
            new Email("test@example.com"),
            new Password("password123")
        );
        User user = User.create(command.email(), command.password(), new UserName("홍길동"));

        given(loadUserPort.findByEmail(command.email().value())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(command.password().encryptedPassword(), user.password().encryptedPassword())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> loginUserService.login(command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(UserApplicationErrorCode.LOGIN_FAILED.getMessage());

        verify(loadUserPort, times(1)).findByEmail(command.email().value());
        verify(passwordEncoder, times(1)).matches(command.password().encryptedPassword(), user.password().encryptedPassword());
    }
}
