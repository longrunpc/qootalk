package com.lrchan.qootalk.infrastructure.persistence.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.lrchan.qootalk.domain.user.UserRole;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    @DisplayName("UserEntity 저장 및 조회 테스트")
    void should_saveAndFind_when_validUser() {
        // given
        UserEntity userEntity = new UserEntity(
            "test@example.com",
            "password123",
            "홍길동",
            UserRole.USER
        );

        // when
        UserEntity savedUserEntity = userJpaRepository.save(userEntity);

        // then
        assertThat(savedUserEntity.id()).isNotNull();
        assertThat(savedUserEntity.email()).isEqualTo("test@example.com");
        assertThat(savedUserEntity.password()).isEqualTo("password123");
        assertThat(savedUserEntity.name()).isEqualTo("홍길동");
        assertThat(savedUserEntity.role()).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("이메일 중복 시 예외 발생")
    void should_throwException_when_duplicateEmail() {
        // given
        UserEntity userEntity1 = new UserEntity(
            "test@example.com",
            "password123",
            "홍길동",
            UserRole.USER
        );
        UserEntity userEntity2 = new UserEntity(
            "test@example.com",
            "password123",
            "김동동",
            UserRole.USER
        ); // 빌더 패턴으로 사용할까 고민
        userJpaRepository.save(userEntity1);

        // when & then
        assertThatThrownBy(() -> userJpaRepository.save(userEntity2))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Nested
    @DisplayName("이메일 조회 테스트")
    class FindByEmailTest {
        @Test
        @DisplayName("이메일 존재 시")
        void should_findByEmail_when_validEmail() {
            // given
            UserEntity userEntity = new UserEntity(
                "test@example.com",
                "password123",
                "홍길동",
                UserRole.USER
            );
            userJpaRepository.save(userEntity);

            // when
            UserEntity foundUserEntity = userJpaRepository.findByEmail("test@example.com").orElseThrow();

            // then
            assertThat(foundUserEntity.id()).isNotNull();
            assertThat(foundUserEntity.email()).isEqualTo("test@example.com");
            assertThat(foundUserEntity.password()).isEqualTo("password123");
            assertThat(foundUserEntity.name()).isEqualTo("홍길동");
            assertThat(foundUserEntity.role()).isEqualTo(UserRole.USER);
        }

        @Test
        @DisplayName("이메일 조회 실패 시 빈 값 반환")
        void should_throwException_when_invalidEmail() {
            // when & then
            assertThat(userJpaRepository.findByEmail("invalid@example.com")).isEmpty();
        }
    }

    @Nested
    @DisplayName("이메일 존재 여부 테스트")
    class ExistsByEmailTest {
        @Test
        @DisplayName("이메일 존재 시")
        void should_existsByEmail_when_validEmail() {
            // given
            UserEntity userEntity = new UserEntity(
                "test@example.com",
                "password123",
                "홍길동",
                UserRole.USER
            );

            userJpaRepository.save(userEntity);

            // when
            boolean exists = userJpaRepository.existsByEmail("test@example.com");

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("이메일 존재 하지 않을 시")
        void should_existsByEmail_when_invalidEmail() {
            // when
            boolean exists = userJpaRepository.existsByEmail("invalid@example.com");

            // then
            assertThat(exists).isFalse();
        }
    }
}
