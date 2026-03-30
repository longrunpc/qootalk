package com.lrchan.qootalk.infrastructure.security.provider;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import com.lrchan.qootalk.common.exception.InfrastructureException;
import com.lrchan.qootalk.infrastructure.common.error.AuthErrorCode;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("JwtAuthenticationValidator 단위 테스트")
class JwtAuthenticationValidatorTest {

    private JwtAuthenticationValidator validator;
    private final String testSalt = "testSecretKeyForJwtAuthenticationValidator1234567890";
    private final long testAccessExpiration = 1000 * 60;
    private SecretKey testKey;

    @BeforeEach
    void setUp() {
        validator = new JwtAuthenticationValidator();
        ReflectionTestUtils.setField(validator, "salt", testSalt);
        validator.init();
        
        testKey = Keys.hmacShaKeyFor(testSalt.getBytes(StandardCharsets.UTF_8));
    }

    private String createRawToken(String subject, String role, long validityMs) {
        Date now = new Date();
        return Jwts.builder()
                .subject(subject)
                .claim("role", role)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + validityMs))
                .signWith(testKey)
                .compact();
    }

    @Nested
    @DisplayName("토큰 유효성 검증(validateToken) 테스트")
    class ValidateToken {

        @Test
        @DisplayName("올바른 서명과 유효 기간 내의 토큰은 유효성 검증에 성공한다")
        void success() {
            String token = createRawToken("user@test.com", "ROLE_USER", testAccessExpiration);
            validator.validateToken(token);
        }

        @Test
        @DisplayName("잘못된 서명(다른 키로 생성)된 토큰은 InfrastructureException이 발생한다")
        void invalidSignature() {
            SecretKey wrongKey = Keys.hmacShaKeyFor("wrongSecretKey123456789012345678901234567890".getBytes());
            String token = Jwts.builder().subject("test").signWith(wrongKey).compact();
            
            assertThatThrownBy(() -> validator.validateToken(token))
                .isInstanceOf(InfrastructureException.class)
                .hasMessage(AuthErrorCode.INVALID_JWT_SIGNATURE.getMessage());
        }

        @Test
        @DisplayName("형식이 잘못된 문자열은 InfrastructureException이 발생한다")
        void malformed() {
            assertThatThrownBy(() -> validator.validateToken("not.a.jwt.token"))
                .isInstanceOf(InfrastructureException.class)
                .hasMessage(AuthErrorCode.INVALID_JWT_MALFORMED.getMessage());
        }

        @Test
        @DisplayName("만료된 토큰은 InfrastructureException이 발생한다")
        void expired() {
            String token = createRawToken("user@test.com", "ROLE_USER", -1000); // 과거 시점
            assertThatThrownBy(() -> validator.validateToken(token))
                .isInstanceOf(InfrastructureException.class)
                .hasMessage(AuthErrorCode.INVALID_JWT_EXPIRED.getMessage());
        }

        @Test
        @DisplayName("지원되지 않는 토큰은 InfrastructureException이 발생한다")
        void unsupported() {
            String token = Jwts.builder().subject("test").compact();
            assertThatThrownBy(() -> validator.validateToken(token))
                .isInstanceOf(InfrastructureException.class)
                .hasMessage(AuthErrorCode.INVALID_JWT_UNSUPPORTED.getMessage());
        }

        @Test
        @DisplayName("JWT 토큰이 null 또는 빈 문자열일 경우 InfrastructureException이 발생한다")
        void illegalArgument() {
            assertThatThrownBy(() -> validator.validateToken(null))
                .isInstanceOf(InfrastructureException.class)
                .hasMessage(AuthErrorCode.INVALID_JWT_ILLEGAL_ARGUMENT.getMessage());
            assertThatThrownBy(() -> validator.validateToken(""))
                .isInstanceOf(InfrastructureException.class)
                .hasMessage(AuthErrorCode.INVALID_JWT_ILLEGAL_ARGUMENT.getMessage());
        }
    }

    @Nested
    @DisplayName("인증 정보 추출(getAuthentication) 테스트")
    class GetAuthentication {

        @Test
        @DisplayName("토큰의 Claims를 기반으로 올바른 Authentication 객체를 생성한다")
        void success() {
            // given
            String email = "admin@qootalk.com";
            String role = "ROLE_ADMIN";
            String token = createRawToken(email, role, 1000 * 60);

            // when
            Authentication auth = validator.getAuthentication(token);

            // then
            assertThat(auth.getName()).isEqualTo(email);
            assertThat(auth.getAuthorities().iterator().next().getAuthority()).isEqualTo(role);
            assertThat(auth.getCredentials()).isEqualTo(""); // 코드상 ""로 설정됨
        }
    }

    @Nested
    @DisplayName("HTTP 헤더 토큰 추출(resolveToken) 테스트")
    class ResolveToken {

        @Test
        @DisplayName("Authorization 헤더에 Bearer 접두사가 있으면 토큰만 추출한다")
        void success() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getHeader("Authorization")).thenReturn("Bearer my-token-value");

            String resolved = validator.resolveToken(request);

            assertThat(resolved).isEqualTo("my-token-value");
        }

        @Test
        @DisplayName("헤더가 없거나 형식이 다르면 null을 반환한다")
        void fail() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getHeader("Authorization")).thenReturn("Basic abcdefg");

            assertThat(validator.resolveToken(request)).isNull();
        }
    }
}