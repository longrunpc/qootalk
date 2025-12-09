package com.lrchan.qootalk.domain.user.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("UserName VO 테스트")
class UserNameTest {

    @Nested
    @DisplayName("생성")
    class CreateTest {

        @Test
        @DisplayName("유효한 이름으로 생성할 수 있다")
        void should_CreateUserName_When_ValidName() {
            // given
            String validName = "홍길동";

            // when
            UserName userName = new UserName(validName);

            // then
            assertThat(userName.value()).isEqualTo(validName);
        }

        @Test
        @DisplayName("최소 길이(2자) 이름으로 생성할 수 있다")
        void should_CreateUserName_When_MinimumLength() {
            // given
            String minLengthName = "홍길";

            // when
            UserName userName = new UserName(minLengthName);

            // then
            assertThat(userName.value()).isEqualTo(minLengthName);
        }

        @Test
        @DisplayName("최대 길이(20자) 이름으로 생성할 수 있다")
        void should_CreateUserName_When_MaximumLength() {
            // given
            String maxLengthName = "가".repeat(20);

            // when
            UserName userName = new UserName(maxLengthName);

            // then
            assertThat(userName.value()).isEqualTo(maxLengthName);
        }

        @Test
        @DisplayName("영문 이름으로 생성할 수 있다")
        void should_CreateUserName_When_EnglishName() {
            // given
            String englishName = "John Doe";

            // when
            UserName userName = new UserName(englishName);

            // then
            assertThat(userName.value()).isEqualTo(englishName);
        }
    }

    @Nested
    @DisplayName("검증 실패")
    class ValidationFailureTest {

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void should_ThrowException_When_Null() {
            // when & then
            assertThatThrownBy(() -> new UserName(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username cannot be null or blank");
        }

        @Test
        @DisplayName("빈 문자열로 생성하면 예외가 발생한다")
        void should_ThrowException_When_Blank() {
            // when & then
            assertThatThrownBy(() -> new UserName(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username cannot be null or blank");

            assertThatThrownBy(() -> new UserName("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username cannot be null or blank");
        }

        @Test
        @DisplayName("1자 이름으로 생성하면 예외가 발생한다")
        void should_ThrowException_When_TooShort() {
            // when & then
            assertThatThrownBy(() -> new UserName("홍"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username must be between 2 and 20 characters");
        }

        @Test
        @DisplayName("21자 이상 이름으로 생성하면 예외가 발생한다")
        void should_ThrowException_When_TooLong() {
            // given
            String tooLongName = "가".repeat(21);

            // when & then
            assertThatThrownBy(() -> new UserName(tooLongName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username must be between 2 and 20 characters");
        }
    }
}

