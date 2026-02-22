package com.lrchan.qootalk.infrastructure.persistence.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.lrchan.qootalk.domain.user.vo.Token;
import com.lrchan.qootalk.infrastructure.IntegrationTestSupport;

@DisplayName("RefreshTokenRedisAdapter 테스트")
public class RefreshTokenRedisAdapterTest extends IntegrationTestSupport {

    @Autowired
    private RefreshTokenRedisAdapter refreshTokenRedisAdapter;

    @AfterEach
    void cleanUp() {
        refreshTokenRedisAdapter.deleteByUsersPk("test@example.com");
    }

    @Test
    @DisplayName("리프레시 토큰 저장 및 조회 테스트")
    void testSaveRefreshToken() {
        // given
        String userPk = "test@example.com";
        Token refreshToken = new Token("refreshToken", 1000 * 60 * 60 * 24);

        // when
        refreshTokenRedisAdapter.save(userPk, refreshToken);

        // then
        assertThat(refreshTokenRedisAdapter.findByUserPk(userPk)).isPresent();
        assertThat(refreshTokenRedisAdapter.findByUserPk(userPk).get().token()).isEqualTo(refreshToken.token());
        assertThat(refreshTokenRedisAdapter.findByUserPk(userPk).get().expiresIn()).isCloseTo(refreshToken.expiresIn(), within(1000L));
    }

    @Test
    @DisplayName("리프레시 토큰 삭제 테스트")
    void testDeleteRefreshToken() {
        // given
        String userPk = "test@example.com";
        Token refreshToken = new Token("refreshToken", 1000 * 60 * 60 * 24);
        refreshTokenRedisAdapter.save(userPk, refreshToken);

        // when
        refreshTokenRedisAdapter.deleteByUsersPk(userPk);

        // then
        assertThat(refreshTokenRedisAdapter.findByUserPk(userPk)).isNotPresent();
    }
}
