package com.lrchan.qootalk.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "messaging.redis")
public record RedisMessagingProperties(
    Channels channels,
    long presenceTtlSeconds
) {

    public record Channels(
        String chatMessage,
        String userPresence
    ) {
    }
}
