package com.lrchan.qootalk.application.user.service;

import java.time.LocalDateTime;
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

import com.lrchan.qootalk.application.user.dto.command.DeleateProfileImageCommand;
import com.lrchan.qootalk.application.user.dto.result.UserQueryResult;
import com.lrchan.qootalk.application.user.error.UserApplicationErrorCode;
import com.lrchan.qootalk.application.user.port.out.DeleteFilePort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.application.user.port.out.SaveUserPort;
import com.lrchan.qootalk.common.exception.ApplicationException;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.UserRole;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;
import com.lrchan.qootalk.domain.user.vo.Email;
import com.lrchan.qootalk.domain.user.vo.Password;
import com.lrchan.qootalk.domain.user.vo.ProfileImageUrl;
import com.lrchan.qootalk.domain.user.vo.StatusMessage;
import com.lrchan.qootalk.domain.user.vo.UserName;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteProfileImageService 테스트")
public class DeleteProfileImageServiceTest {
    
    @Mock
    private DeleteFilePort deleteProfileImagePort;

    @Mock
    private SaveUserPort saveUserPort;

    @Mock
    private LoadUserPort loadUserPort;

    @InjectMocks
    private DeleteProfileImageService deleteProfileImageService;

    @Test
    @DisplayName("프로필 이미지 삭제 성공: 프로필 이미지가 존재하는 경우")
    public void should_DeleteProfileImage_When_ProfileImageIsDeleted() {
        // given
        User user = User.reconstruct(1L, new Email("test@example.com"), new Password("password123"), new UserName("홍길동"), new ProfileImageUrl("https://example.com/profile.jpg"), new StatusMessage(""), UserRole.USER, LocalDateTime.now(), LocalDateTime.now(), null);
        DeleateProfileImageCommand command = new DeleateProfileImageCommand(
            1L,
            new ProfileImageUrl("https://example.com/profile.jpg")
        );

        given(loadUserPort.findById(command.userId())).willReturn(Optional.of(user));
        given(saveUserPort.save(any(User.class))).willReturn(user);

        // when
        UserQueryResult result = deleteProfileImageService.delete(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(user.email().value());
        assertThat(result.name()).isEqualTo(user.name().value());
        assertThat(result.profileImageUrl()).isNull();

        verify(loadUserPort, times(1)).findById(command.userId());
        verify(saveUserPort, times(1)).save(any(User.class));
        verify(deleteProfileImagePort, times(1)).delete(command.profileImageUrl().value());
    }

    @Test
    @DisplayName("프로필 이미지 삭제 실패: 프로필 이미지가 존재하지 않는 경우")
    public void should_FailToDeleteProfileImage_When_ProfileImageIsNotFound() {
        // given
        User user = User.reconstruct(1L, new Email("test@example.com"), new Password("password123"), new UserName("홍길동"), new ProfileImageUrl(null), new StatusMessage(""), UserRole.USER, LocalDateTime.now(), LocalDateTime.now(), null);
        DeleateProfileImageCommand command = new DeleateProfileImageCommand(
            1L,
            new ProfileImageUrl(null)
        );

        given(loadUserPort.findById(command.userId())).willReturn(Optional.of(user));

        // when
        UserQueryResult result = deleteProfileImageService.delete(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(user.email().value());
        assertThat(result.name()).isEqualTo(user.name().value());
        assertThat(result.profileImageUrl()).isNull();

        verify(loadUserPort, times(1)).findById(command.userId());
        verify(saveUserPort, times(0)).save(any(User.class));
        verify(deleteProfileImagePort, times(0)).delete(command.profileImageUrl().value());
    }

    @Test
    @DisplayName("프로필 이미지 삭제 실패: 사용자가 존재하지 않는 경우")
    public void should_FailToDeleteProfileImage_When_UserNotFound() {
        // given
        DeleateProfileImageCommand command = new DeleateProfileImageCommand(
            1L,
            new ProfileImageUrl("https://example.com/profile.jpg")
        );

        given(loadUserPort.findById(command.userId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> deleteProfileImageService.delete(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());

        verify(loadUserPort, times(1)).findById(command.userId());
        verify(saveUserPort, times(0)).save(any(User.class));
        verify(deleteProfileImagePort, times(0)).delete(command.profileImageUrl().value());
    }

    @Test
    @DisplayName("프로필 이미지 삭제 실패: 프로필 이미지 URL이 일치하지 않는 경우")
    public void should_FailToDeleteProfileImage_When_ProfileImageUrlIsNotMatched() {
        // given
        User user = User.reconstruct(1L, new Email("test@example.com"), new Password("password123"), new UserName("홍길동"), new ProfileImageUrl("https://example.com/profile.jpg"), new StatusMessage(""), UserRole.USER, LocalDateTime.now(), LocalDateTime.now(), null);
        DeleateProfileImageCommand command = new DeleateProfileImageCommand(
            1L,
            new ProfileImageUrl("https://example.com/wrong.jpg")
        );

        given(loadUserPort.findById(command.userId())).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> deleteProfileImageService.delete(command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(UserApplicationErrorCode.USER_PROFILE_IMAGE_URL_MISMATCH.getMessage());

        verify(loadUserPort, times(1)).findById(command.userId());
        verify(saveUserPort, times(0)).save(any(User.class));
        verify(deleteProfileImagePort, times(0)).delete(command.profileImageUrl().value());
    }
}
