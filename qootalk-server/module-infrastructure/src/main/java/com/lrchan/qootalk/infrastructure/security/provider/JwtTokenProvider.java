package com.lrchan.qootalk.infrastructure.security.provider;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String salt;

    private SecretKey secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String userPk, String role) {
        Claims claims = Jwts.claims().subject(userPk).build();

        Date now = new Date();
        return Jwts.builder()
            .claims(claims)
            .issuedAt(now)
            .expiration(new Date(now.getTime() + expiration))
            .signWith(secretKey)
            .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
    
    public String getUserPk(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            // 잘못된 JWT 서명일 때 (추후 로깅 처리)
        } catch (ExpiredJwtException e) {
            // 만료된 JWT 토큰일 때
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 JWT 토큰일 때
        } catch (IllegalArgumentException e) {
            // JWT 토큰이 잘못되었을 때
        }
        return false;
    }
}