package com.lrchan.qootalk.domain.chat.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Path VO 테스트")
class PathTest {

    @Nested
    @DisplayName("생성")
    class CreateTest {

        @Test
        @DisplayName("유효한 경로로 생성할 수 있다")
        void should_CreatePath_When_ValidPath() {
            // given
            String validPath = "uploads/files/";

            // when
            Path path = new Path(validPath);

            // then
            assertThat(path.value()).isEqualTo(validPath);
        }

        @Test
        @DisplayName("최소 길이(2자) 경로로 생성할 수 있다")
        void should_CreatePath_When_MinimumLength() {
            // given
            String minLengthPath = "a/";

            // when
            Path path = new Path(minLengthPath);

            // then
            assertThat(path.value()).isEqualTo(minLengthPath);
        }

        @Test
        @DisplayName("최대 길이(200자) 경로로 생성할 수 있다")
        void should_CreatePath_When_MaximumLength() {
            // given
            String maxLengthPath = "a".repeat(199) + "/";

            // when
            Path path = new Path(maxLengthPath);

            // then
            assertThat(path.value()).isEqualTo(maxLengthPath);
        }

        @Test
        @DisplayName("특수문자가 포함된 경로로 생성할 수 있다")
        void should_CreatePath_When_ContainsSpecialCharacters() {
            // given
            String pathWithSpecialChars = "uploads/test-files_2024.01.01/";

            // when
            Path path = new Path(pathWithSpecialChars);

            // then
            assertThat(path.value()).isEqualTo(pathWithSpecialChars);
        }
    }

    @Nested
    @DisplayName("검증 실패")
    class ValidationFailureTest {

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void should_ThrowException_When_Null() {
            // when & then
            assertThatThrownBy(() -> new Path(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Path cannot be null or blank");
        }

        @Test
        @DisplayName("빈 문자열로 생성하면 예외가 발생한다")
        void should_ThrowException_When_Blank() {
            // when & then
            assertThatThrownBy(() -> new Path(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Path cannot be null or blank");

            assertThatThrownBy(() -> new Path("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Path cannot be null or blank");
        }

        @Test
        @DisplayName("1자 경로로 생성하면 예외가 발생한다")
        void should_ThrowException_When_TooShort() {
            // when & then
            assertThatThrownBy(() -> new Path("a"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Path must be between 2 and 200 characters");
        }

        @Test
        @DisplayName("201자 이상 경로로 생성하면 예외가 발생한다")
        void should_ThrowException_When_TooLong() {
            // given
            String tooLongPath = "a".repeat(200) + "/";

            // when & then
            assertThatThrownBy(() -> new Path(tooLongPath))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Path must be between 2 and 200 characters");
        }

        @Test
        @DisplayName("슬래시로 끝나지 않는 경로로 생성하면 예외가 발생한다")
        void should_ThrowException_When_NotEndsWithSlash() {
            // when & then
            assertThatThrownBy(() -> new Path("uploads/files"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Path must end with a slash");
        }

        @Test
        @DisplayName("연속된 슬래시가 포함된 경로로 생성하면 예외가 발생한다")
        void should_ThrowException_When_ContainsMultipleSlashes() {
            // when & then
            assertThatThrownBy(() -> new Path("uploads//files/"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Path cannot contain multiple slashes");
        }

        @Test
        @DisplayName("상위 디렉토리 참조가 포함된 경로로 생성하면 예외가 발생한다")
        void should_ThrowException_When_ContainsParentDirectory() {
            // when & then
            assertThatThrownBy(() -> new Path("uploads/../files/"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Path cannot contain parent directory");
        }

        @Test
        @DisplayName("허용되지 않는 특수문자가 포함된 경로로 생성하면 예외가 발생한다")
        void should_ThrowException_When_ContainsInvalidCharacters() {
            // when & then
            assertThatThrownBy(() -> new Path("uploads/test@files/"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Path must contain only letters, numbers, slashes, and special characters");
        }
    }

    @Nested
    @DisplayName("equals & hashCode")
    class EqualsAndHashCodeTest {

        @Test
        @DisplayName("같은 값을 가진 Path는 동등하다")
        void should_BeEqual_When_SameValue() {
            // given
            String value = "uploads/files/";
            Path path1 = new Path(value);
            Path path2 = new Path(value);

            // when & then
            assertThat(path1).isEqualTo(path2);
            assertThat(path1.hashCode()).isEqualTo(path2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 Path는 동등하지 않다")
        void should_NotBeEqual_When_DifferentValue() {
            // given
            Path path1 = new Path("uploads/files1/");
            Path path2 = new Path("uploads/files2/");

            // when & then
            assertThat(path1).isNotEqualTo(path2);
        }
    }
}

