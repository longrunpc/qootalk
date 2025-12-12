package com.lrchan.qootalk.domain.chat.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("FileSize VO 테스트")
class FileSizeTest {

    private static final long GLOBAL_MAX = 500L * 1024 * 1024; // 500MB

    @Nested
    @DisplayName("생성")
    class CreateTest {

        @Test
        @DisplayName("유효한 파일 크기로 생성할 수 있다")
        void should_CreateFileSize_When_ValidSize() {
            // given
            Long validSize = 1024L;

            // when
            FileSize fileSize = new FileSize(validSize);

            // then
            assertThat(fileSize.value()).isEqualTo(validSize);
        }

        @Test
        @DisplayName("0 바이트 파일 크기로 생성할 수 있다")
        void should_CreateFileSize_When_Zero() {
            // given
            Long zeroSize = 0L;

            // when
            FileSize fileSize = new FileSize(zeroSize);

            // then
            assertThat(fileSize.value()).isEqualTo(zeroSize);
        }

        @Test
        @DisplayName("최대 허용 크기로 생성할 수 있다")
        void should_CreateFileSize_When_MaximumSize() {
            // given
            Long maxSize = GLOBAL_MAX;

            // when
            FileSize fileSize = new FileSize(maxSize);

            // then
            assertThat(fileSize.value()).isEqualTo(maxSize);
        }

        @Test
        @DisplayName("큰 파일 크기로 생성할 수 있다")
        void should_CreateFileSize_When_LargeSize() {
            // given
            Long largeSize = 100L * 1024 * 1024; // 100MB

            // when
            FileSize fileSize = new FileSize(largeSize);

            // then
            assertThat(fileSize.value()).isEqualTo(largeSize);
        }
    }

    @Nested
    @DisplayName("검증 실패")
    class ValidationFailureTest {

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void should_ThrowException_When_Null() {
            // when & then
            assertThatThrownBy(() -> new FileSize(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File size cannot be null or negative");
        }

        @Test
        @DisplayName("음수 값으로 생성하면 예외가 발생한다")
        void should_ThrowException_When_Negative() {
            // when & then
            assertThatThrownBy(() -> new FileSize(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File size cannot be null or negative");
        }

        @Test
        @DisplayName("최대 허용 크기를 초과하면 예외가 발생한다")
        void should_ThrowException_When_ExceedsMaximum() {
            // given
            Long exceedsMax = GLOBAL_MAX + 1;

            // when & then
            assertThatThrownBy(() -> new FileSize(exceedsMax))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("File size cannot be greater than");
        }
    }

    @Nested
    @DisplayName("equals & hashCode")
    class EqualsAndHashCodeTest {

        @Test
        @DisplayName("같은 값을 가진 FileSize는 동등하다")
        void should_BeEqual_When_SameValue() {
            // given
            Long value = 1024L;
            FileSize fileSize1 = new FileSize(value);
            FileSize fileSize2 = new FileSize(value);

            // when & then
            assertThat(fileSize1).isEqualTo(fileSize2);
            assertThat(fileSize1.hashCode()).isEqualTo(fileSize2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 FileSize는 동등하지 않다")
        void should_NotBeEqual_When_DifferentValue() {
            // given
            FileSize fileSize1 = new FileSize(1024L);
            FileSize fileSize2 = new FileSize(2048L);

            // when & then
            assertThat(fileSize1).isNotEqualTo(fileSize2);
        }
    }
}

