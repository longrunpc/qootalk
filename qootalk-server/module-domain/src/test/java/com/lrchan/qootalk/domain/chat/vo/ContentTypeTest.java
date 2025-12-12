package com.lrchan.qootalk.domain.chat.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ContentType VO 테스트")
class ContentTypeTest {

    @Nested
    @DisplayName("생성")
    class CreateTest {

        @Test
        @DisplayName("유효한 MIME 타입으로 생성할 수 있다")
        void should_CreateContentType_When_ValidMimeType() {
            // given
            String validMimeType = "image/png";

            // when
            ContentType contentType = new ContentType(validMimeType);

            // then
            assertThat(contentType.value()).isEqualTo(validMimeType);
        }

        @Test
        @DisplayName("대문자가 포함된 MIME 타입은 소문자로 정규화된다")
        void should_NormalizeToLowerCase_When_ContainsUpperCase() {
            // given
            String upperCaseMimeType = "IMAGE/PNG";

            // when
            ContentType contentType = new ContentType(upperCaseMimeType);

            // then
            assertThat(contentType.value()).isEqualTo("image/png");
        }

        @Test
        @DisplayName("앞뒤 공백이 포함된 MIME 타입은 정규화된다")
        void should_TrimWhitespace_When_ContainsWhitespace() {
            // given
            String mimeTypeWithWhitespace = "  image/png  ";

            // when
            ContentType contentType = new ContentType(mimeTypeWithWhitespace);

            // then
            assertThat(contentType.value()).isEqualTo("image/png");
        }

        @Test
        @DisplayName("다양한 유효한 MIME 타입으로 생성할 수 있다")
        void should_CreateContentType_When_VariousValidMimeTypes() {
            // given & when & then
            assertThat(new ContentType("application/pdf").value()).isEqualTo("application/pdf");
            assertThat(new ContentType("video/mp4").value()).isEqualTo("video/mp4");
            assertThat(new ContentType("audio/mpeg").value()).isEqualTo("audio/mpeg");
            assertThat(new ContentType("text/plain").value()).isEqualTo("text/plain");
            assertThat(new ContentType("application/json").value()).isEqualTo("application/json");
        }
    }

    @Nested
    @DisplayName("검증 실패")
    class ValidationFailureTest {

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void should_ThrowException_When_Null() {
            // when & then
            assertThatThrownBy(() -> new ContentType(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Content type cannot be null");
        }

        @Test
        @DisplayName("빈 문자열로 생성하면 예외가 발생한다")
        void should_ThrowException_When_Blank() {
            // when & then
            assertThatThrownBy(() -> new ContentType(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Content type cannot be blank");

            assertThatThrownBy(() -> new ContentType("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Content type cannot be blank");
        }

        @Test
        @DisplayName("잘못된 형식의 MIME 타입으로 생성하면 예외가 발생한다")
        void should_ThrowException_When_InvalidFormat() {
            // when & then
            assertThatThrownBy(() -> new ContentType("invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid MIME type format");

            assertThatThrownBy(() -> new ContentType("image"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid MIME type format");

            assertThatThrownBy(() -> new ContentType("image/"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid MIME type format");
        }

        @Test
        @DisplayName("차단된 MIME 타입으로 생성하면 예외가 발생한다")
        void should_ThrowException_When_BlockedType() {
            // when & then
            assertThatThrownBy(() -> new ContentType("application/octet-stream"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Blocked content type: application/octet-stream");

            assertThatThrownBy(() -> new ContentType("application/x-msdownload"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Blocked content type: application/x-msdownload");

            assertThatThrownBy(() -> new ContentType("application/x-sh"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Blocked content type: application/x-sh");

            assertThatThrownBy(() -> new ContentType("application/java-archive"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Blocked content type: application/java-archive");
        }
    }

    @Nested
    @DisplayName("equals & hashCode")
    class EqualsAndHashCodeTest {

        @Test
        @DisplayName("같은 값을 가진 ContentType은 동등하다")
        void should_BeEqual_When_SameValue() {
            // given
            String value = "image/png";
            ContentType contentType1 = new ContentType(value);
            ContentType contentType2 = new ContentType(value);

            // when & then
            assertThat(contentType1).isEqualTo(contentType2);
            assertThat(contentType1.hashCode()).isEqualTo(contentType2.hashCode());
        }

        @Test
        @DisplayName("대소문자만 다른 ContentType은 동등하다 (정규화 후)")
        void should_BeEqual_When_DifferentCase() {
            // given
            ContentType contentType1 = new ContentType("IMAGE/PNG");
            ContentType contentType2 = new ContentType("image/png");

            // when & then
            assertThat(contentType1).isEqualTo(contentType2);
            assertThat(contentType1.hashCode()).isEqualTo(contentType2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 ContentType은 동등하지 않다")
        void should_NotBeEqual_When_DifferentValue() {
            // given
            ContentType contentType1 = new ContentType("image/png");
            ContentType contentType2 = new ContentType("image/jpeg");

            // when & then
            assertThat(contentType1).isNotEqualTo(contentType2);
        }
    }
}

