package com.lrchan.qootalk.application.user.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lrchan.qootalk.application.user.dto.command.UpdateStatusMessageCommand;
import com.lrchan.qootalk.application.user.dto.result.UserQueryResult;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.application.user.port.out.SaveUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.UserRole;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;
import com.lrchan.qootalk.domain.user.vo.Email;
import com.lrchan.qootalk.domain.user.vo.Password;
import com.lrchan.qootalk.domain.user.vo.StatusMessage;
import com.lrchan.qootalk.domain.user.vo.UserName;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateStatusMessageService 테스트")
public class UpdateStatusMessageServiceTest {
    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private SaveUserPort saveUserPort;

    @InjectMocks
    private UpdateStatusMessageService updateStatusMessageService;

    @Test
    @DisplayName("상태 메시지 업데이트 성공")
    public void should_UpdateStatusMessage_When_StatusMessageIsUpdated() {
        // given
        User user = User.reconstruct(1L, new Email("test@example.com"), new Password("password123"), new UserName("홍길동"), null, new StatusMessage(""), UserRole.USER, LocalDateTime.now(), LocalDateTime.now(), null);
        UpdateStatusMessageCommand command = new UpdateStatusMessageCommand(
            1L,
            "안녕하세요!"
        );

        given(loadUserPort.findById(command.userId())).willReturn(Optional.of(user));
        given(saveUserPort.save(any(User.class))).willReturn(user);

        // when
        UserQueryResult result = updateStatusMessageService.update(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(user.email().value());
        assertThat(result.name()).isEqualTo(user.name().value());
        assertThat(result.statusMessage()).isEqualTo(command.statusMessage());

        verify(loadUserPort, times(1)).findById(command.userId());
        verify(saveUserPort, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("상태 메시지 업데이트 실패: 사용자가 존재하지 않는 경우")
    public void should_FailToUpdateStatusMessage_When_UserNotFound() {
        // given
        UpdateStatusMessageCommand command = new UpdateStatusMessageCommand(
            1L,
            "안녕하세요!"
        );

        given(loadUserPort.findById(command.userId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> updateStatusMessageService.update(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());

        verify(loadUserPort, times(1)).findById(command.userId());
        verify(saveUserPort, times(0)).save(any(User.class));
    }
}
