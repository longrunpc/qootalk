package com.lrchan.qootalk.infrastructure.persistence.redis;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.lrchan.qootalk.application.user.port.out.RefreshTokenPort;
import com.lrchan.qootalk.domain.user.vo.Token;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RefreshTokenRedisAdapter implements RefreshTokenPort {
    
    private final StringRedisTemplate redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    @Override
    public void save(String userPk, Token refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + userPk;

        redisTemplate.opsForValue().set(
            key, 
            Objects.requireNonNull(refreshToken.token()),
            refreshToken.expiresIn(),
            TimeUnit.MILLISECONDS
        );
    }

    @Override
    public Optional<Token> findByUserPk(String userPk) {
        String key = REFRESH_TOKEN_PREFIX + userPk;
        String token = redisTemplate.opsForValue().get(key);

        if (token == null) {
            return Optional.empty();
        }

        long expiresIn = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
        return Optional.of(new Token(token, expiresIn));
    }

    @Override
    public void deleteByUserPk(String userPk) {
        String key = REFRESH_TOKEN_PREFIX + userPk;
        redisTemplate.delete(key);
    }
}
