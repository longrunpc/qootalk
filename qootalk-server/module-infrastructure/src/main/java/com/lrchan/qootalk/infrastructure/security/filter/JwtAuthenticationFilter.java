package com.lrchan.qootalk.infrastructure.security.filter;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.lrchan.qootalk.infrastructure.security.provider.JwtAuthenticationValidator;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtAuthenticationValidator jwtAuthenticationValidator;

    public JwtAuthenticationFilter(JwtAuthenticationValidator jwtAuthenticationValidator) {
        this.jwtAuthenticationValidator = jwtAuthenticationValidator;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtAuthenticationValidator.resolveToken(request);

        jwtAuthenticationValidator.validateToken(token);
        
        Authentication authentication = jwtAuthenticationValidator.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
