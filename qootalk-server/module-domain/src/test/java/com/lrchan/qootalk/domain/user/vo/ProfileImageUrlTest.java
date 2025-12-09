package com.lrchan.qootalk.domain.user.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ProfileImageUrl VO 테스트")
class ProfileImageUrlTest {

    @Nested
    @DisplayName("생성")
    class CreateTest {

        @Test
        @DisplayName("유효한 HTTP URL로 생성할 수 있다")
        void should_CreateProfileImageUrl_When_ValidHttpUrl() {
            // given
            String validUrl = "http://example.com/profile.jpg";

            // when
            ProfileImageUrl profileImageUrl = new ProfileImageUrl(validUrl);

            // then
            assertThat(profileImageUrl.value()).isEqualTo(validUrl);
        }

        @Test
        @DisplayName("유효한 HTTPS URL로 생성할 수 있다")
        void should_CreateProfileImageUrl_When_ValidHttpsUrl() {
            // given
            String validUrl = "https://example.com/profile.jpg";

            // when
            ProfileImageUrl profileImageUrl = new ProfileImageUrl(validUrl);

            // then
            assertThat(profileImageUrl.value()).isEqualTo(validUrl);
        }

        @Test
        @DisplayName("다양한 유효한 URL 형식을 생성할 수 있다")
        void should_CreateProfileImageUrl_When_VariousValidFormats() {
            // given & when & then
            assertThat(new ProfileImageUrl("https://example.com/image.png").value())
                .isEqualTo("https://example.com/image.png");
            assertThat(new ProfileImageUrl("http://subdomain.example.com/path/to/image.jpg").value())
                .isEqualTo("http://subdomain.example.com/path/to/image.jpg");
            assertThat(new ProfileImageUrl("https://example.com:8080/image.gif").value())
                .isEqualTo("https://example.com:8080/image.gif");
        }
    }

    @Nested
    @DisplayName("검증 실패")
    class ValidationFailureTest {

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void should_ThrowException_When_Null() {
            // when & then
            assertThatThrownBy(() -> new ProfileImageUrl(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Profile image URL cannot be null or blank");
        }

        @Test
        @DisplayName("빈 문자열로 생성하면 예외가 발생한다")
        void should_ThrowException_When_Blank() {
            // when & then
            assertThatThrownBy(() -> new ProfileImageUrl(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Profile image URL cannot be null or blank");

            assertThatThrownBy(() -> new ProfileImageUrl("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Profile image URL cannot be null or blank");
        }

        @Test
        @DisplayName("http:// 또는 https://로 시작하지 않으면 예외가 발생한다")
        void should_ThrowException_When_InvalidFormat() {
            // when & then
            assertThatThrownBy(() -> new ProfileImageUrl("ftp://example.com/image.jpg"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid profile image URL format");

            assertThatThrownBy(() -> new ProfileImageUrl("example.com/image.jpg"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid profile image URL format");

            assertThatThrownBy(() -> new ProfileImageUrl("/path/to/image.jpg"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid profile image URL format");

            assertThatThrownBy(() -> new ProfileImageUrl("image.jpg"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid profile image URL format");
        }
    }
}

