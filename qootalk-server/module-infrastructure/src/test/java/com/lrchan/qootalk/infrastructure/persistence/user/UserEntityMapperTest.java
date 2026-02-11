package com.lrchan.qootalk.infrastructure.persistence.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.UserRole;
import com.lrchan.qootalk.domain.user.vo.Email;
import com.lrchan.qootalk.domain.user.vo.Password;
import com.lrchan.qootalk.domain.user.vo.ProfileImageUrl;
import com.lrchan.qootalk.domain.user.vo.StatusMessage;
import com.lrchan.qootalk.domain.user.vo.UserName;

@DisplayName("UserEntityMapper 테스트")
class UserEntityMapperTest {

    @Nested
    @DisplayName("도메인으로 변환")
    class ToDomainTest {

        @Test
        @DisplayName("UserEntity를 User 도메인으로 변환할 수 있다")
        void should_ConvertToDomain_When_ValidEntity() {
            // given
            LocalDateTime now = LocalDateTime.now();
            UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .email("test@example.com")
                .password("encrypted_password")
                .name("홍길동")
                .profileImageUrl("https://example.com/profile.jpg")
                .statusMessage("안녕하세요!")
                .role(UserRole.USER)
                .createdAt(now)
                .updatedAt(now)
                .deletedAt(null)
                .build();

            // when
            User user = UserEntityMapper.toDomain(userEntity);

            // then
            assertThat(user.id()).isEqualTo(1L);
            assertThat(user.email().value()).isEqualTo("test@example.com");
            assertThat(user.password().encryptedPassword()).isEqualTo("encrypted_password");
            assertThat(user.name().value()).isEqualTo("홍길동");
            assertThat(user.profileImageUrl().value()).isEqualTo("https://example.com/profile.jpg");
            assertThat(user.statusMessage().value()).isEqualTo("안녕하세요!");
            assertThat(user.role()).isEqualTo(UserRole.USER);
            assertThat(user.createdAt()).isEqualTo(now);
            assertThat(user.updatedAt()).isEqualTo(now);
            assertThat(user.deletedAt()).isNull();
        }

        @Test
        @DisplayName("profileImageUrl이 null인 UserEntity를 User 도메인으로 변환할 수 있다")
        void should_ConvertToDomain_When_ProfileImageUrlIsNull() {
            // given
            LocalDateTime now = LocalDateTime.now();
            UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .email("test@example.com")
                .password("encrypted_password")
                .name("홍길동")
                .profileImageUrl(null)
                .statusMessage("안녕하세요!")
                .role(UserRole.USER)
                .createdAt(now)
                .updatedAt(now)
                .deletedAt(null)
                .build();

            // when
            User user = UserEntityMapper.toDomain(userEntity);

            // then
            assertThat(user.profileImageUrl().value()).isNull();
            assertThat(user.statusMessage().value()).isEqualTo("안녕하세요!");
        }

        @Test
        @DisplayName("statusMessage가 null인 UserEntity를 User 도메인으로 변환할 수 있다")
        void should_ConvertToDomain_When_StatusMessageIsNull() {
            // given
            LocalDateTime now = LocalDateTime.now();
            UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .email("test@example.com")
                .password("encrypted_password")
                .name("홍길동")
                .profileImageUrl("https://example.com/profile.jpg")
                .statusMessage(null)
                .role(UserRole.USER)
                .createdAt(now)
                .updatedAt(now)
                .deletedAt(null)
                .build();

            // when
            User user = UserEntityMapper.toDomain(userEntity);

            // then
            assertThat(user.profileImageUrl().value()).isEqualTo("https://example.com/profile.jpg");
            assertThat(user.statusMessage().value()).isEqualTo("");
        }

        @Test
        @DisplayName("deletedAt이 설정된 UserEntity를 User 도메인으로 변환할 수 있다")
        void should_ConvertToDomain_When_DeletedAtIsSet() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime deletedAt = LocalDateTime.now();
            UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .email("test@example.com")
                .password("encrypted_password")
                .name("홍길동")
                .profileImageUrl(null)
                .statusMessage(null)
                .role(UserRole.USER)
                .createdAt(now)
                .updatedAt(now)
                .deletedAt(deletedAt)
                .build();

            // when
            User user = UserEntityMapper.toDomain(userEntity);

            // then
            assertThat(user.deletedAt()).isEqualTo(deletedAt);
            assertThat(user.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("엔티티로 변환")
    class ToEntityTest {

        @Test
        @DisplayName("User 도메인을 UserEntity로 변환할 수 있다")
        void should_ConvertToEntity_When_ValidDomain() {
            // given
            LocalDateTime now = LocalDateTime.now();
            User user = User.reconstruct(
                1L,
                new Email("test@example.com"),
                new Password("encrypted_password"),
                new UserName("홍길동"),
                new ProfileImageUrl("https://example.com/profile.jpg"),
                new StatusMessage("안녕하세요!"),
                UserRole.USER,
                now,
                now,
                null
            );

            // when
            UserEntity userEntity = UserEntityMapper.toEntity(user);

            // then
            assertThat(userEntity.getId()).isEqualTo(1L);
            assertThat(userEntity.getEmail()).isEqualTo("test@example.com");
            assertThat(userEntity.getPassword()).isEqualTo("encrypted_password");
            assertThat(userEntity.getName()).isEqualTo("홍길동");
            assertThat(userEntity.getProfileImageUrl()).isEqualTo("https://example.com/profile.jpg");
            assertThat(userEntity.getStatusMessage()).isEqualTo("안녕하세요!");
            assertThat(userEntity.getRole()).isEqualTo(UserRole.USER);
            assertThat(userEntity.getCreatedAt()).isEqualTo(now);
            assertThat(userEntity.getUpdatedAt()).isEqualTo(now);
            assertThat(userEntity.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("profileImageUrl이 null인 User 도메인을 UserEntity로 변환할 수 있다")
        void should_ConvertToEntity_When_ProfileImageUrlIsNull() {
            // given
            LocalDateTime now = LocalDateTime.now();
            User user = User.reconstruct(
                1L,
                new Email("test@example.com"),
                new Password("encrypted_password"),
                new UserName("홍길동"),
                new ProfileImageUrl(null),
                new StatusMessage("안녕하세요!"),
                UserRole.USER,
                now,
                now,
                null
            );

            // when
            UserEntity userEntity = UserEntityMapper.toEntity(user);

            // then
            assertThat(userEntity.getProfileImageUrl()).isNull();
            assertThat(userEntity.getStatusMessage()).isEqualTo("안녕하세요!");
        }

        @Test
        @DisplayName("statusMessage가 null인 User 도메인을 UserEntity로 변환할 수 있다")
        void should_ConvertToEntity_When_StatusMessageIsNull() {
            // given
            LocalDateTime now = LocalDateTime.now();
            User user = User.reconstruct(
                1L,
                new Email("test@example.com"),
                new Password("encrypted_password"),
                new UserName("홍길동"),
                new ProfileImageUrl("https://example.com/profile.jpg"),
                null,
                UserRole.USER,
                now,
                now,
                null
            );

            // when
            UserEntity userEntity = UserEntityMapper.toEntity(user);

            // then
            assertThat(userEntity.getProfileImageUrl()).isEqualTo("https://example.com/profile.jpg");
            assertThat(userEntity.getStatusMessage()).isEqualTo("");
        }

        @Test
        @DisplayName("deletedAt이 설정된 User 도메인을 UserEntity로 변환할 수 있다")
        void should_ConvertToEntity_When_DeletedAtIsSet() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime deletedAt = LocalDateTime.now();
            User user = User.reconstruct(
                1L,
                new Email("test@example.com"),
                new Password("encrypted_password"),
                new UserName("홍길동"),
                null,
                null,
                UserRole.USER,
                now,
                now,
                deletedAt
            );

            // when
            UserEntity userEntity = UserEntityMapper.toEntity(user);

            // then
            assertThat(userEntity.getDeletedAt()).isEqualTo(deletedAt);
            assertThat(userEntity.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("양방향 변환")
    class RoundTripTest {

        @Test
        @DisplayName("User 도메인을 UserEntity로 변환하고 다시 User 도메인으로 변환하면 동일한 값이 유지된다")
        void should_MaintainValues_When_RoundTrip() {
            // given
            LocalDateTime now = LocalDateTime.now();
            User originalUser = User.reconstruct(
                1L,
                new Email("test@example.com"),
                new Password("encrypted_password"),
                new UserName("홍길동"),
                new ProfileImageUrl("https://example.com/profile.jpg"),
                new StatusMessage("안녕하세요!"),
                UserRole.USER,
                now,
                now,
                null
            );

            // when
            UserEntity userEntity = UserEntityMapper.toEntity(originalUser);
            User convertedUser = UserEntityMapper.toDomain(userEntity);

            // then
            assertThat(convertedUser.id()).isEqualTo(originalUser.id());
            assertThat(convertedUser.email().value()).isEqualTo(originalUser.email().value());
            assertThat(convertedUser.password().encryptedPassword()).isEqualTo(originalUser.password().encryptedPassword());
            assertThat(convertedUser.name().value()).isEqualTo(originalUser.name().value());
            assertThat(convertedUser.profileImageUrl().value()).isEqualTo(originalUser.profileImageUrl().value());
            assertThat(convertedUser.statusMessage().value()).isEqualTo(originalUser.statusMessage().value());
            assertThat(convertedUser.role()).isEqualTo(originalUser.role());
            assertThat(convertedUser.createdAt()).isEqualTo(originalUser.createdAt());
            assertThat(convertedUser.updatedAt()).isEqualTo(originalUser.updatedAt());
            assertThat(convertedUser.deletedAt()).isEqualTo(originalUser.deletedAt());
        }

        @Test
        @DisplayName("null 값이 포함된 User 도메인을 양방향 변환해도 올바르게 처리된다")
        void should_HandleNullValues_When_RoundTrip() {
            // given
            LocalDateTime now = LocalDateTime.now();
            User originalUser = User.reconstruct(
                1L,
                new Email("test@example.com"),
                new Password("encrypted_password"),
                new UserName("홍길동"),
                new ProfileImageUrl(null),
                null,
                UserRole.USER,
                now,
                now,
                null
            );

            // when
            UserEntity userEntity = UserEntityMapper.toEntity(originalUser);
            User convertedUser = UserEntityMapper.toDomain(userEntity);

            // then
            assertThat(convertedUser.profileImageUrl().value()).isNull();
            assertThat(convertedUser.statusMessage().value()).isEqualTo("");
        }
    }
}

