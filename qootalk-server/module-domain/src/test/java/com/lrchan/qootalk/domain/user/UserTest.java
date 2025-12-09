package com.lrchan.qootalk.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.lrchan.qootalk.domain.user.vo.Email;
import com.lrchan.qootalk.domain.user.vo.ProfileImageUrl;
import com.lrchan.qootalk.domain.user.vo.UserName;

@DisplayName("User 도메인 테스트")
class UserTest {

    @Nested
    @DisplayName("유저 생성")
    class CreateUserTest {

        @Test
        @DisplayName("유저를 생성할 때 기본값이 올바르게 설정되어야 한다")
        void should_CreateUser_When_ValidInput() {
            // given
            Email email = new Email("test@example.com");
            String password = "password123";
            UserName name = new UserName("홍길동");

            // when
            User user = User.create(email, password, name);

            // then
            assertThat(user.email()).isEqualTo(email);
            assertThat(user.name()).isEqualTo(name);
            assertThat(user.role()).isEqualTo(UserRole.USER);
            assertThat(user.statusMessage()).isEqualTo("");
            assertThat(user.id()).isNull();
            assertThat(user.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("유저를 생성할 때 생성 시간과 수정 시간이 설정되어야 한다")
        void should_SetCreatedAtAndUpdatedAt_When_CreateUser() {
            // given
            Email email = new Email("test@example.com");
            String password = "password123";
            UserName name = new UserName("홍길동");

            // when
            User user = User.create(email, password, name);

            // then
            assertThat(user.id()).isNull();
            assertThat(user.isDeleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("이름 변경")
    class ChangeNameTest {

        @Test
        @DisplayName("이름을 변경할 때 이름이 업데이트되고 수정 시간이 갱신되어야 한다")
        void should_UpdateName_When_ChangeName() {
            // given
            User user = User.create(
                new Email("test@example.com"),
                "password123",
                new UserName("홍길동")
            );
            UserName newName = new UserName("김철수");

            // when
            user.changeName(newName);

            // then
            assertThat(user.name()).isEqualTo(newName);
            assertThat(user.name().value()).isEqualTo("김철수");
        }

        @Test
        @DisplayName("여러 번 이름을 변경할 때 마지막 이름이 유지되어야 한다")
        void should_KeepLastName_When_ChangeNameMultipleTimes() {
            // given
            User user = User.create(
                new Email("test@example.com"),
                "password123",
                new UserName("홍길동")
            );

            // when
            user.changeName(new UserName("김철수"));
            user.changeName(new UserName("이영희"));

            // then
            assertThat(user.name().value()).isEqualTo("이영희");
        }
    }

    @Nested
    @DisplayName("프로필 이미지 URL 변경")
    class ChangeProfileImageUrlTest {

        @Test
        @DisplayName("프로필 이미지 URL을 변경할 때 URL이 업데이트되고 수정 시간이 갱신되어야 한다")
        void should_UpdateProfileImageUrl_When_ChangeProfileImageUrl() {
            // given
            User user = User.create(
                new Email("test@example.com"),
                "password123",
                new UserName("홍길동")
            );
            ProfileImageUrl newProfileImageUrl = new ProfileImageUrl("https://example.com/profile.jpg");

            // when
            user.changeProfileImageUrl(newProfileImageUrl);

            // then
            assertThat(user.profileImageUrl()).isEqualTo(newProfileImageUrl);
            assertThat(user.profileImageUrl().value()).isEqualTo("https://example.com/profile.jpg");
        }

        @Test
        @DisplayName("여러 번 프로필 이미지를 변경할 때 마지막 이미지가 유지되어야 한다")
        void should_KeepLastProfileImage_When_ChangeProfileImageMultipleTimes() {
            // given
            User user = User.create(
                new Email("test@example.com"),
                "password123",
                new UserName("홍길동")
            );

            // when
            user.changeProfileImageUrl(new ProfileImageUrl("https://example.com/image1.jpg"));
            user.changeProfileImageUrl(new ProfileImageUrl("https://example.com/image2.jpg"));

            // then
            assertThat(user.profileImageUrl().value()).isEqualTo("https://example.com/image2.jpg");
        }
    }

    @Nested
    @DisplayName("상태 메시지 변경")
    class ChangeStatusMessageTest {

        @Test
        @DisplayName("상태 메시지를 변경할 때 메시지가 업데이트되고 수정 시간이 갱신되어야 한다")
        void should_UpdateStatusMessage_When_ChangeStatusMessage() {
            // given
            User user = User.create(
                new Email("test@example.com"),
                "password123",
                new UserName("홍길동")
            );
            String newStatusMessage = "안녕하세요!";

            // when
            user.changeStatusMessage(newStatusMessage);

            // then
            assertThat(user.statusMessage()).isEqualTo(newStatusMessage);
        }

        @Test
        @DisplayName("상태 메시지를 빈 문자열로 변경할 때 빈 문자열로 업데이트되어야 한다")
        void should_UpdateStatusMessageToEmpty_When_ChangeStatusMessageToEmpty() {
            // given
            User user = User.create(
                new Email("test@example.com"),
                "password123",
                new UserName("홍길동")
            );
            user.changeStatusMessage("기존 메시지");

            // when
            user.changeStatusMessage("");

            // then
            assertThat(user.statusMessage()).isEqualTo("");
        }
    }

    @Nested
    @DisplayName("조회")
    class QueryTest {

        @Test
        @DisplayName("이메일을 조회할 때 올바른 이메일이 반환되어야 한다")
        void should_ReturnEmail_When_GetEmail() {
            // given
            Email email = new Email("test@example.com");
            User user = User.create(email, "password123", new UserName("홍길동"));

            // when
            Email result = user.email();

            // then
            assertThat(result).isEqualTo(email);
            assertThat(result.value()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("이름을 조회할 때 올바른 이름이 반환되어야 한다")
        void should_ReturnName_When_GetName() {
            // given
            UserName name = new UserName("홍길동");
            User user = User.create(new Email("test@example.com"), "password123", name);

            // when
            UserName result = user.name();

            // then
            assertThat(result).isEqualTo(name);
            assertThat(result.value()).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("프로필 이미지 URL을 조회할 때 올바른 URL이 반환되어야 한다")
        void should_ReturnProfileImageUrl_When_GetProfileImageUrl() {
            // given
            User user = User.create(
                new Email("test@example.com"),
                "password123",
                new UserName("홍길동")
            );
            ProfileImageUrl profileImageUrl = new ProfileImageUrl("https://example.com/profile.jpg");
            user.changeProfileImageUrl(profileImageUrl);

            // when
            ProfileImageUrl result = user.profileImageUrl();

            // then
            assertThat(result).isEqualTo(profileImageUrl);
            assertThat(result.value()).isEqualTo("https://example.com/profile.jpg");
        }

        @Test
        @DisplayName("역할을 조회할 때 기본값으로 USER가 반환되어야 한다")
        void should_ReturnUserRole_When_GetRole() {
            // given
            User user = User.create(
                new Email("test@example.com"),
                "password123",
                new UserName("홍길동")
            );

            // when
            UserRole role = user.role();

            // then
            assertThat(role).isEqualTo(UserRole.USER);
        }
    }

    @Nested
    @DisplayName("소프트 삭제")
    class SoftDeleteTest {

        @Test
        @DisplayName("유저를 소프트 삭제하면 삭제 상태가 되어야 한다")
        void should_MarkAsDeleted_When_SoftDelete() {
            // given
            User user = User.create(
                new Email("test@example.com"),
                "password123",
                new UserName("홍길동")
            );

            // when
            user.softDelete();

            // then
            assertThat(user.isDeleted()).isTrue();
        }
    }
}

