package com.lrchan.qootalk.domain.chat.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;

@DisplayName("FileName VO 테스트")
class FileNameTest {

    @Nested
    @DisplayName("생성")
    class CreateTest {

        @Test
        @DisplayName("유효한 파일명으로 생성할 수 있다")
        void should_CreateFileName_When_ValidName() {
            // given
            String validName = "test-file.pdf";

            // when
            FileName fileName = new FileName(validName);

            // then
            assertThat(fileName.value()).isEqualTo(validName);
        }

        @Test
        @DisplayName("최소 길이(2자) 파일명으로 생성할 수 있다")
        void should_CreateFileName_When_MinimumLength() {
            // given
            String minLengthName = "ab";

            // when
            FileName fileName = new FileName(minLengthName);

            // then
            assertThat(fileName.value()).isEqualTo(minLengthName);
        }

        @Test
        @DisplayName("최대 길이(100자) 파일명으로 생성할 수 있다")
        void should_CreateFileName_When_MaximumLength() {
            // given
            String maxLengthName = "a".repeat(100);

            // when
            FileName fileName = new FileName(maxLengthName);

            // then
            assertThat(fileName.value()).isEqualTo(maxLengthName);
        }

        @Test
        @DisplayName("특수문자가 포함된 파일명으로 생성할 수 있다")
        void should_CreateFileName_When_ContainsSpecialCharacters() {
            // given
            String nameWithSpecialChars = "test_file-name.123%+-";

            // when
            FileName fileName = new FileName(nameWithSpecialChars);

            // then
            assertThat(fileName.value()).isEqualTo(nameWithSpecialChars);
        }
    }

    @Nested
    @DisplayName("검증 실패")
    class ValidationFailureTest {

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void should_ThrowException_When_Null() {
            // when & then
            assertThatThrownBy(() -> new FileName(null))
                .isInstanceOf(DomainException.class)
                .hasMessage(ChatErrorCode.CHAT_FILE_METADATA_INVALID_FILE_NAME.getMessage());
        }

        @Test
        @DisplayName("빈 문자열로 생성하면 예외가 발생한다")
        void should_ThrowException_When_Blank() {
            // when & then
            assertThatThrownBy(() -> new FileName(""))
                .isInstanceOf(DomainException.class)
                .hasMessage(ChatErrorCode.CHAT_FILE_METADATA_INVALID_FILE_NAME.getMessage());

            assertThatThrownBy(() -> new FileName("   "))
                .isInstanceOf(DomainException.class)
                .hasMessage(ChatErrorCode.CHAT_FILE_METADATA_INVALID_FILE_NAME.getMessage());
        }

        @Test
        @DisplayName("1자 파일명으로 생성하면 예외가 발생한다")
        void should_ThrowException_When_TooShort() {
            // when & then
            assertThatThrownBy(() -> new FileName("a"))
                .isInstanceOf(DomainException.class)
                .hasMessage(ChatErrorCode.CHAT_FILE_METADATA_INVALID_FILE_NAME.getMessage());
        }

        @Test
        @DisplayName("101자 이상 파일명으로 생성하면 예외가 발생한다")
        void should_ThrowException_When_TooLong() {
            // given
            String tooLongName = "a".repeat(101);

            // when & then
            assertThatThrownBy(() -> new FileName(tooLongName))
                .isInstanceOf(DomainException.class)
                .hasMessage(ChatErrorCode.CHAT_FILE_METADATA_INVALID_FILE_NAME.getMessage());
        }

        @Test
        @DisplayName("허용되지 않는 특수문자가 포함된 파일명으로 생성하면 예외가 발생한다")
        void should_ThrowException_When_ContainsInvalidCharacters() {
            // when & then
            assertThatThrownBy(() -> new FileName("test@file.pdf"))
                .isInstanceOf(DomainException.class)
                .hasMessage(ChatErrorCode.CHAT_FILE_METADATA_INVALID_FILE_NAME.getMessage());

            assertThatThrownBy(() -> new FileName("test file.pdf"))
                .isInstanceOf(DomainException.class)
                .hasMessage(ChatErrorCode.CHAT_FILE_METADATA_INVALID_FILE_NAME.getMessage());
        }
    }

    @Nested
    @DisplayName("equals & hashCode")
    class EqualsAndHashCodeTest {

        @Test
        @DisplayName("같은 값을 가진 FileName은 동등하다")
        void should_BeEqual_When_SameValue() {
            // given
            String value = "test-file.pdf";
            FileName fileName1 = new FileName(value);
            FileName fileName2 = new FileName(value);

            // when & then
            assertThat(fileName1).isEqualTo(fileName2);
            assertThat(fileName1.hashCode()).isEqualTo(fileName2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 FileName은 동등하지 않다")
        void should_NotBeEqual_When_DifferentValue() {
            // given
            FileName fileName1 = new FileName("test1.pdf");
            FileName fileName2 = new FileName("test2.pdf");

            // when & then
            assertThat(fileName1).isNotEqualTo(fileName2);
        }
    }
}

