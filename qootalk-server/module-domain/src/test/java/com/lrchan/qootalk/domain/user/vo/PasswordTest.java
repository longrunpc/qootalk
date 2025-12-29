package com.lrchan.qootalk.domain.user.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

@DisplayName("Password VO 테스트")
class PasswordTest {

    @Nested
    @DisplayName("생성")
    class CreateTest {

        @Test
        @DisplayName("유효한 암호화된 비밀번호로 생성할 수 있다")
        void should_CreatePassword_When_ValidEncryptedPassword() {
            // given
            String validEncryptedPassword = "encrypted_password_hash_value";

            // when
            Password password = new Password(validEncryptedPassword);

            // then
            assertThat(password.encryptedPassword()).isEqualTo(validEncryptedPassword);
        }

        @Test
        @DisplayName("다양한 유효한 암호화된 비밀번호 형식을 생성할 수 있다")
        void should_CreatePassword_When_VariousValidFormats() {
            // given & when & then
            assertThat(new Password("$2a$10$encrypted").encryptedPassword()).isEqualTo("$2a$10$encrypted");
            assertThat(new Password("hashed_value_123456").encryptedPassword()).isEqualTo("hashed_value_123456");
            assertThat(new Password("ABCDEF1234567890").encryptedPassword()).isEqualTo("ABCDEF1234567890");
        }
    }

    @Nested
    @DisplayName("검증 실패")
    class ValidationFailureTest {

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void should_ThrowException_When_Null() {
            // when & then
            assertThatThrownBy(() -> new Password(null))
                .isInstanceOf(DomainException.class)
                .hasMessage(UserErrorCode.USER_INVALID_PASSWORD.getMessage());
        }

        @Test
        @DisplayName("빈 문자열로 생성하면 예외가 발생한다")
        void should_ThrowException_When_Blank() {
            // when & then
            assertThatThrownBy(() -> new Password(""))
                .isInstanceOf(DomainException.class)
                .hasMessage(UserErrorCode.USER_INVALID_PASSWORD.getMessage());

            assertThatThrownBy(() -> new Password("   "))
                .isInstanceOf(DomainException.class)
                .hasMessage(UserErrorCode.USER_INVALID_PASSWORD.getMessage());
        }
    }
}
