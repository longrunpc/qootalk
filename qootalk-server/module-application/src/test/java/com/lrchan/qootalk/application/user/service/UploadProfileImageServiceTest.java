package com.lrchan.qootalk.application.user.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lrchan.qootalk.application.user.dto.command.UploadProfileImageCommand;
import com.lrchan.qootalk.application.user.dto.result.UserQueryResult;
import com.lrchan.qootalk.application.user.port.out.DeleteFilePort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.application.user.port.out.SaveUserPort;
import com.lrchan.qootalk.application.user.port.out.UploadFilePort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.common.storage.vo.StorageResource;
import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.UserRole;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;
import com.lrchan.qootalk.domain.user.vo.Email;
import com.lrchan.qootalk.domain.user.vo.Password;
import com.lrchan.qootalk.domain.user.vo.ProfileImageUrl;
import com.lrchan.qootalk.domain.user.vo.StatusMessage;
import com.lrchan.qootalk.domain.user.vo.UserName;

@ExtendWith(MockitoExtension.class)
@DisplayName("UploadProfileImageService 테스트")
public class UploadProfileImageServiceTest {

    @Mock
    private UploadFilePort uploadProfileImagePort;
    
    @Mock
    private DeleteFilePort deleteProfileImagePort;

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private SaveUserPort saveUserPort;

    @InjectMocks
    private UploadProfileImageService uploadProfileImageService;

    @Test
    @DisplayName("프로필 이미지 업로드 성공 : 업로드 전 프로필 이미지가 존재하지 않는 경우")
    public void should_UploadProfileImage_When_ProfileImageIsUploaded_And_PreviousProfileImageIsNotFound() {
        // given
        User user = User.reconstruct(1L, new Email("test@example.com"), new Password("password123"), new UserName("홍길동"), new ProfileImageUrl(null), new StatusMessage(""), UserRole.USER, LocalDateTime.now(), LocalDateTime.now(), null);
        UploadProfileImageCommand command = new UploadProfileImageCommand(
            1L,
            new ByteArrayInputStream("profile.jpg".getBytes()),
            "test.jpg",
            "image/jpeg",
            (long) "profile.jpg".getBytes().length
        );
        StorageResource storageResource = new StorageResource(
            "profile/" + command.userId(), 
            "test.jpg", 
            "image/jpeg", 
            (long) "profile.jpg".getBytes().length
        );
    
        given(loadUserPort.findById(command.userId())).willReturn(Optional.of(user));
        given(saveUserPort.save(any(User.class))).willReturn(user);
        given(uploadProfileImagePort.upload(command.inputStream(), storageResource)).willReturn("https://example.com/profile.jpg");

        // when
        UserQueryResult result = uploadProfileImageService.upload(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(user.email().value());
        assertThat(result.name()).isEqualTo(user.name().value());
        assertThat(result.profileImageUrl()).isEqualTo("https://example.com/profile.jpg");
    
        verify(loadUserPort, times(1)).findById(command.userId());
        verify(saveUserPort, times(1)).save(any(User.class));
        verify(uploadProfileImagePort, times(1)).upload(command.inputStream(), storageResource);
        verify(deleteProfileImagePort, times(0)).delete(any(String.class));
    }

    @Test
    @DisplayName("프로필 이미지 업로드 성공 : 업로드 전 프로필 이미지가 존재하는 경우")
    public void should_UploadProfileImage_When_ProfileImageIsUploaded_And_PreviousProfileImageIsFound() {
        // given
        User user = User.reconstruct(1L, new Email("test@example.com"), new Password("password123"), new UserName("홍길동"), new ProfileImageUrl("https://example.com/profile.jpg"), new StatusMessage(""), UserRole.USER, LocalDateTime.now(), LocalDateTime.now(), null);
        UploadProfileImageCommand command = new UploadProfileImageCommand(
            1L,
            new ByteArrayInputStream("profile.jpg".getBytes()),
            "test.jpg",
            "image/jpeg",
            (long) "profile.jpg".getBytes().length
        );
        StorageResource storageResource = new StorageResource(
            "profile/" + command.userId(), 
            "test.jpg", 
            "image/jpeg", 
            (long) "profile.jpg".getBytes().length
        );
    
        given(loadUserPort.findById(command.userId())).willReturn(Optional.of(user));
        given(saveUserPort.save(any(User.class))).willReturn(user);
        given(uploadProfileImagePort.upload(command.inputStream(), storageResource)).willReturn("https://example.com/profile.jpg");

        // when
        UserQueryResult result = uploadProfileImageService.upload(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(user.email().value());
        assertThat(result.name()).isEqualTo(user.name().value());
        assertThat(result.profileImageUrl()).isEqualTo("https://example.com/profile.jpg");
    
        verify(loadUserPort, times(1)).findById(command.userId());
        verify(saveUserPort, times(1)).save(any(User.class));
        verify(uploadProfileImagePort, times(1)).upload(command.inputStream(), storageResource);
        verify(deleteProfileImagePort, times(1)).delete("https://example.com/profile.jpg");
    }

    @Test
    @DisplayName("프로필 이미지 업로드 실패 : 사용자가 존재하지 않는 경우")
    public void should_FailToUploadProfileImage_When_UserNotFound() {
        // given
        UploadProfileImageCommand command = new UploadProfileImageCommand(
            1L,
            new ByteArrayInputStream("profile.jpg".getBytes()),
            "test.jpg",
            "image/jpeg",
            (long) "profile.jpg".getBytes().length
        );

        given(loadUserPort.findById(command.userId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> uploadProfileImageService.upload(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());

        verify(loadUserPort, times(1)).findById(command.userId());
        verify(saveUserPort, times(0)).save(any(User.class));
        verify(uploadProfileImagePort, times(0)).upload(any(InputStream.class), any(StorageResource.class));
        verify(deleteProfileImagePort, times(0)).delete(any(String.class));
    }

    @Test
    @DisplayName("프로필 이미지 업로드 실패 : 파일 저장소에 프로필 이미지 업로드 실패")
    public void should_FailToUploadProfileImage_When_UploadProfileImageFailed() {
        // given
        User user = User.reconstruct(1L, new Email("test@example.com"), new Password("password123"), new UserName("홍길동"), new ProfileImageUrl(null), new StatusMessage(""), UserRole.USER, LocalDateTime.now(), LocalDateTime.now(), null);
        UploadProfileImageCommand command = new UploadProfileImageCommand(
            1L,
            new ByteArrayInputStream("profile.jpg".getBytes()),
            "test.jpg",
            "image/jpeg",
            (long) "profile.jpg".getBytes().length 
        );
        StorageResource storageResource = new StorageResource(
            "profile/" + command.userId(), 
            "test.jpg", 
            "image/jpeg", 
            (long) "profile.jpg".getBytes().length
        );
    
        given(loadUserPort.findById(command.userId())).willReturn(Optional.of(user));
        given(uploadProfileImagePort.upload(command.inputStream(), storageResource)).willThrow(new RuntimeException("Upload failed"));

        // when & then
        assertThatThrownBy(() -> uploadProfileImageService.upload(command))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Upload failed");

        verify(loadUserPort, times(1)).findById(command.userId());
        verify(saveUserPort, times(0)).save(any(User.class));
        verify(uploadProfileImagePort, times(1)).upload(any(InputStream.class), any(StorageResource.class));
        verify(deleteProfileImagePort, times(0)).delete(any(String.class));
    }
}
