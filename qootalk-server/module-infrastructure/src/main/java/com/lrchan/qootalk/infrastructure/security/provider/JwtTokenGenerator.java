package com.lrchan.qootalk.infrastructure.security.provider;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import io.jsonwebtoken.Jwts;
import com.lrchan.qootalk.application.user.port.out.TokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenGenerator implements TokenProvider {

    @Value("${jwt.secret}")
    private String salt;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String createAccessToken(String userPk, String role) {
        Date now = new Date();
        return Jwts.builder()
            .subject(userPk)
            .issuer("qootalk")
            .claim("role", role)
            .issuedAt(now)
            .expiration(new Date(now.getTime() + accessExpiration))
            .signWith(secretKey)
            .compact();
    }

    @Override
    public String createRefreshToken(String userPk) {
        Date now = new Date();
        return Jwts.builder()
            .subject(userPk)
            .id(UUID.randomUUID().toString())
            .issuer("qootalk")
            .issuedAt(now)
            .expiration(new Date(now.getTime() + refreshExpiration))
            .signWith(secretKey)
            .compact();
    }
}
