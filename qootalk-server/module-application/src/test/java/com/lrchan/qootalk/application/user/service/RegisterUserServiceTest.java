package com.lrchan.qootalk.application.user.service;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lrchan.qootalk.application.user.port.out.SaveUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;
import com.lrchan.qootalk.domain.user.vo.Email;
import com.lrchan.qootalk.domain.user.vo.Password;
import com.lrchan.qootalk.domain.user.vo.UserName;
import com.lrchan.qootalk.application.user.dto.command.RegisterUserCommand;
import com.lrchan.qootalk.application.user.dto.result.UserQueryResult;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterUserService 테스트")
public class RegisterUserServiceTest {

    @Mock
    private SaveUserPort saveUserPort;

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUserService registerUserService;

    @Test
    @DisplayName("회원가입 성공: 이메일이 중복되지 않는 경우")
    public void should_RegisterUser_When_EmailIsNotDuplicated() {
        // given
        RegisterUserCommand command = new RegisterUserCommand(
            new Email("test@example.com"),
            new Password("password123"),
            new UserName("홍길동")
        );
        
        given(loadUserPort.findByEmail(command.email().value())).willReturn(Optional.empty());
        given(passwordEncoder.encode(command.password().encryptedPassword())).willReturn("encodedPassword");
        given(saveUserPort.save(any(User.class))).willReturn(User.create(command.email(), command.password(), command.name()));

        // when
        UserQueryResult result = registerUserService.register(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(command.email().value());
        assertThat(result.name()).isEqualTo(command.name().value());
        
        verify(loadUserPort, times(1)).findByEmail(command.email().value());
        verify(passwordEncoder, times(1)).encode(command.password().encryptedPassword());
        verify(saveUserPort, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패: 이메일이 중복된 경우")
    public void should_FailToRegisterUser_When_EmailIsDuplicated() {
        // given
        RegisterUserCommand command = new RegisterUserCommand(
            new Email("test@example.com"),
            new Password("password123"),
            new UserName("홍길동")
        );

        given(loadUserPort.findByEmail(command.email().value())).willReturn(Optional.of(User.create(command.email(), command.password(), command.name())));

        // when & then
        assertThatThrownBy(() -> registerUserService.register(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(UserErrorCode.USER_ALREADY_EXISTS.getMessage());

        verify(loadUserPort, times(1)).findByEmail(command.email().value());
        verify(passwordEncoder, times(0)).encode(command.password().encryptedPassword());
        verify(saveUserPort, times(0)).save(any(User.class));
    }
}
