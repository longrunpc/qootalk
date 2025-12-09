package com.lrchan.qootalk.domain.user.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Email VO 테스트")
class EmailTest {

    @Nested
    @DisplayName("생성")
    class CreateTest {

        @Test
        @DisplayName("유효한 이메일로 생성할 수 있다")
        void should_CreateEmail_When_ValidEmail() {
            // given
            String validEmail = "test@example.com";

            // when
            Email email = new Email(validEmail);

            // then
            assertThat(email.value()).isEqualTo(validEmail);
        }

        @Test
        @DisplayName("다양한 유효한 이메일 형식을 생성할 수 있다")
        void should_CreateEmail_When_VariousValidFormats() {
            // given & when & then
            assertThat(new Email("user.name@example.com").value()).isEqualTo("user.name@example.com");
            assertThat(new Email("user+tag@example.co.kr").value()).isEqualTo("user+tag@example.co.kr");
            assertThat(new Email("user_name@example-domain.com").value()).isEqualTo("user_name@example-domain.com");
            assertThat(new Email("123@example.com").value()).isEqualTo("123@example.com");
        }
    }

    @Nested
    @DisplayName("검증 실패")
    class ValidationFailureTest {

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void should_ThrowException_When_Null() {
            // when & then
            assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email cannot be null or blank");
        }

        @Test
        @DisplayName("빈 문자열로 생성하면 예외가 발생한다")
        void should_ThrowException_When_Blank() {
            // when & then
            assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email cannot be null or blank");

            assertThatThrownBy(() -> new Email("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email cannot be null or blank");
        }

        @Test
        @DisplayName("잘못된 이메일 형식으로 생성하면 예외가 발생한다")
        void should_ThrowException_When_InvalidFormat() {
            // when & then
            assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email format");

            assertThatThrownBy(() -> new Email("@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email format");

            assertThatThrownBy(() -> new Email("user@"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email format");

            assertThatThrownBy(() -> new Email("user@example"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email format");

            assertThatThrownBy(() -> new Email("user @example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email format");
        }
    }
}

