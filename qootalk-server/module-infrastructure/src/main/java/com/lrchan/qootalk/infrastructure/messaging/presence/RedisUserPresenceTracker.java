package com.lrchan.qootalk.infrastructure.messaging.presence;

import java.time.Duration;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.Cursor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisUserPresenceTracker {

    private static final String USER_PRESENCE_KEY_PREFIX = "qootalk:user:presence:";

    private final StringRedisTemplate stringRedisTemplate;
    private final Duration userPresenceTtl;

    public void markConnected(Long userId, String connectionId) {
        String key = presenceKey(userId, connectionId);
        stringRedisTemplate.opsForValue().set(key, "ONLINE", userPresenceTtl);
    }

    public void refreshConnection(Long userId, String connectionId) {
        String key = presenceKey(userId, connectionId);
        stringRedisTemplate.expire(key, userPresenceTtl);
    }

    public void disconnect(Long userId, String connectionId) {
        stringRedisTemplate.delete(presenceKey(userId, connectionId));
    }

    public boolean isConnected(Long userId) {
        ScanOptions options = ScanOptions.scanOptions()
            .match(USER_PRESENCE_KEY_PREFIX + userId + ":*")
            .count(20)
            .build();

        return Boolean.TRUE.equals(stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> {
            try (Cursor<byte[]> cursor = connection.scan(options)) {
                return cursor.hasNext();
            }
        }));
    }

    private String presenceKey(Long userId, String connectionId) {
        return USER_PRESENCE_KEY_PREFIX + userId + ":" + connectionId;
    }
}
