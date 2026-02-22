package com.lrchan.qootalk.infrastructure.security.filter;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import com.lrchan.qootalk.common.exception.InfrastructureException;
import com.lrchan.qootalk.infrastructure.security.provider.JwtAuthenticationValidator;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter 테스트")
public class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtAuthenticationValidator jwtAuthenticationValidator;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
    

    @Test
    @DisplayName("유효한 토큰이 제공될 시 인증 객체가 SecurityContextHolder에 설정되어야 한다")
    public void should_SetAuthentication_When_ValidToken() throws ServletException, IOException {
        // given
        String token = "token";
        Authentication authentication = new UsernamePasswordAuthenticationToken("user@test.com", "", null);

        given(jwtAuthenticationValidator.resolveToken(request)).willReturn(token);
        given(jwtAuthenticationValidator.validateToken(token)).willReturn(true);
        given(jwtAuthenticationValidator.getAuthentication(token)).willReturn(authentication);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication resultAuthentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(resultAuthentication).isEqualTo(authentication);
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtAuthenticationValidator, times(1)).resolveToken(request);
        verify(jwtAuthenticationValidator, times(1)).validateToken(token);
        verify(jwtAuthenticationValidator, times(1)).getAuthentication(token);
    }

    @Test
    @DisplayName("유효하지 않은 토큰일 경우 InfrastructureException이 발생해야 한다")
    public void should_ThrowInfrastructureException_When_InvalidToken() {
        // given
        String token = "invalidToken";

        given(jwtAuthenticationValidator.resolveToken(request)).willReturn(token);
        given(jwtAuthenticationValidator.validateToken(token)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain))
            .isInstanceOf(InfrastructureException.class);
            
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("토큰이 존재하지 않으면 InfrastructureException이 발생해야 한다")
    public void should_ThrowInfrastructureException_When_TokenNotFound() {
        // given
        String token = null;

        given(jwtAuthenticationValidator.resolveToken(request)).willReturn(token);

        // when & then
        assertThatThrownBy(() -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain))
            .isInstanceOf(InfrastructureException.class);
            
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
