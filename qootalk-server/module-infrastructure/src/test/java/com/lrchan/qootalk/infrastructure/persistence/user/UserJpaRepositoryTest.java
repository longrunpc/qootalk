package com.lrchan.qootalk.infrastructure.persistence.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.lrchan.qootalk.domain.user.UserRole;

import static org.assertj.core.api.Assertions.assertThat;

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
    
}
