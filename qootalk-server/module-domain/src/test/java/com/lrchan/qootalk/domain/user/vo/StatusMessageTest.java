package com.lrchan.qootalk.domain.user.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

@DisplayName("StatusMessage VO 테스트")
class StatusMessageTest {

    @Nested
    @DisplayName("생성")
    class CreateTest {

        @Test
        @DisplayName("유효한 상태 메시지로 생성할 수 있다")
        void should_CreateStatusMessage_When_ValidMessage() {
            // given
            String validMessage = "안녕하세요!";

            // when
            StatusMessage statusMessage = new StatusMessage(validMessage);

            // then
            assertThat(statusMessage.value()).isEqualTo(validMessage);
        }

        @Test
        @DisplayName("빈 문자열로 생성할 수 있다")
        void should_CreateStatusMessage_When_EmptyString() {
            // given
            String emptyMessage = "";

            // when
            StatusMessage statusMessage = new StatusMessage(emptyMessage);

            // then
            assertThat(statusMessage.value()).isEqualTo("");
        }

        @Test
        @DisplayName("null 값으로 생성하면 빈 문자열로 변환된다")
        void should_CreateStatusMessage_When_Null() {
            // when
            StatusMessage statusMessage = new StatusMessage(null);

            // then
            assertThat(statusMessage.value()).isEqualTo("");
        }

        @Test
        @DisplayName("최대 길이(100자) 상태 메시지로 생성할 수 있다")
        void should_CreateStatusMessage_When_MaximumLength() {
            // given
            String maxLengthMessage = "가".repeat(100);

            // when
            StatusMessage statusMessage = new StatusMessage(maxLengthMessage);

            // then
            assertThat(statusMessage.value()).isEqualTo(maxLengthMessage);
        }

        @Test
        @DisplayName("다양한 유효한 상태 메시지 형식을 생성할 수 있다")
        void should_CreateStatusMessage_When_VariousValidFormats() {
            // given & when & then
            assertThat(new StatusMessage("Hello World").value()).isEqualTo("Hello World");
            assertThat(new StatusMessage("안녕하세요! 반갑습니다.").value()).isEqualTo("안녕하세요! 반갑습니다.");
            assertThat(new StatusMessage("1234567890").value()).isEqualTo("1234567890");
            assertThat(new StatusMessage("특수문자!@#$%^&*()").value()).isEqualTo("특수문자!@#$%^&*()");
        }
    }

    @Nested
    @DisplayName("검증 실패")
    class ValidationFailureTest {

        @Test
        @DisplayName("101자 이상 상태 메시지로 생성하면 예외가 발생한다")
        void should_ThrowException_When_TooLong() {
            // given
            String tooLongMessage = "가".repeat(101);

            // when & then
            assertThatThrownBy(() -> new StatusMessage(tooLongMessage))
                .isInstanceOf(DomainException.class)
                .hasMessage(UserErrorCode.USER_INVALID_STATUS_MESSAGE.getMessage());
        }

        @Test
        @DisplayName("매우 긴 상태 메시지로 생성하면 예외가 발생한다")
        void should_ThrowException_When_VeryLong() {
            // given
            String veryLongMessage = "가".repeat(200);

            // when & then
            assertThatThrownBy(() -> new StatusMessage(veryLongMessage))
                .isInstanceOf(DomainException.class)
                .hasMessage(UserErrorCode.USER_INVALID_STATUS_MESSAGE.getMessage());
        }
    }
}

