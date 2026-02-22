package com.lrchan.qootalk.infrastructure.security.provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;


@DisplayName("JwtTokenGenerator 테스트")
public class JwtTokenGeneratorTest {
    
    private JwtTokenGenerator jwtTokenGenerator;
    
    private final String testSalt = "testSecretKeyForJwtAuthenticationValidator1234567890";

    @BeforeEach
    void setUp() {
        jwtTokenGenerator = new JwtTokenGenerator();
        ReflectionTestUtils.setField(jwtTokenGenerator, "salt", testSalt);
        ReflectionTestUtils.setField(jwtTokenGenerator, "accessExpiration", 1000 * 60);
        ReflectionTestUtils.setField(jwtTokenGenerator, "refreshExpiration", 1000 * 60 * 60 * 24);
        jwtTokenGenerator.init();
    }

    @Test
    @DisplayName("액세스 토큰 생성 테스트")
    void testCreateToken() {
        // given
        String userPk = "test@example.com";
        String role = "ROLE_USER";

        // when
        String accessToken = jwtTokenGenerator.createAccessToken(userPk, role);

        // then
        assertThat(accessToken).isNotNull();
        SecretKey secretKey = Keys.hmacShaKeyFor(testSalt.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(accessToken).getPayload();
        assertThat(claims.getSubject()).isEqualTo(userPk);
        assertThat(claims.get("role")).isEqualTo(role);
        assertThat(claims.getIssuedAt()).isBefore(new Date());
        assertThat(claims.getExpiration()).isAfter(new Date());
    }

    @Test
    @DisplayName("리프레시 토큰 생성 테스트")
    void testCreateRefreshToken() {
        // given
        String userPk = "test@example.com";

        // when
        String refreshToken = jwtTokenGenerator.createRefreshToken(userPk);

        // then
        assertThat(refreshToken).isNotNull();
        SecretKey secretKey = Keys.hmacShaKeyFor(testSalt.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(refreshToken).getPayload();
        assertThat(claims.getSubject()).isEqualTo(userPk);
        assertThat(claims.getId()).isNotNull();
        assertThat(claims.getIssuedAt()).isBefore(new Date());
        assertThat(claims.getExpiration()).isAfter(new Date());
    }
}
