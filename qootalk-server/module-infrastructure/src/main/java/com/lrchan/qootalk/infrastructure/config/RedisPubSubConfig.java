package com.lrchan.qootalk.infrastructure.config;

import java.time.Duration;
import java.util.Optional;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(RedisMessagingProperties.class)
public class RedisPubSubConfig {

    private static final String DEFAULT_CHAT_MESSAGE_CHANNEL = "qootalk:chat:message";
    private static final String DEFAULT_USER_PRESENCE_CHANNEL = "qootalk:user:presence";
    private static final long DEFAULT_USER_PRESENCE_TTL_SECONDS = 300L;

    @Bean
    ChannelTopic chatMessageChannelTopic(RedisMessagingProperties properties) {
        return new ChannelTopic(resolveChatMessageChannel(properties));
    }

    @Bean
    ChannelTopic userPresenceChannelTopic(RedisMessagingProperties properties) {
        return new ChannelTopic(resolveUserPresenceChannel(properties));
    }

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(
        org.springframework.data.redis.connection.RedisConnectionFactory connectionFactory
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    @Bean
    Duration userPresenceTtl(RedisMessagingProperties properties) {
        return Duration.ofSeconds(resolvePresenceTtlSeconds(properties));
    }

    @Bean
    MessagePublisher redisMessagePublisher(StringRedisTemplate redisTemplate) {
        return new RedisMessagePublisher(redisTemplate);
    }

    public interface MessagePublisher {
        void publish(ChannelTopic topic, Object payload);
    }

    static class RedisMessagePublisher implements MessagePublisher {
        private final StringRedisTemplate redisTemplate;

        RedisMessagePublisher(StringRedisTemplate redisTemplate) {
            this.redisTemplate = redisTemplate;
        }

        @Override
        public void publish(ChannelTopic topic, Object payload) {
            redisTemplate.convertAndSend(topic.getTopic(), payload);
        }
    }

    private String resolveChatMessageChannel(RedisMessagingProperties properties) {
        return Optional.ofNullable(properties)
            .map(RedisMessagingProperties::channels)
            .map(RedisMessagingProperties.Channels::chatMessage)
            .filter(StringUtils::hasText)
            .orElse(DEFAULT_CHAT_MESSAGE_CHANNEL);
    }

    private String resolveUserPresenceChannel(RedisMessagingProperties properties) {
        return Optional.ofNullable(properties)
            .map(RedisMessagingProperties::channels)
            .map(RedisMessagingProperties.Channels::userPresence)
            .filter(StringUtils::hasText)
            .orElse(DEFAULT_USER_PRESENCE_CHANNEL);
    }

    private long resolvePresenceTtlSeconds(RedisMessagingProperties properties) {
        return Optional.ofNullable(properties)
            .map(RedisMessagingProperties::presenceTtlSeconds)
            .filter(ttl -> ttl > 0)
            .orElse(DEFAULT_USER_PRESENCE_TTL_SECONDS);
    }
}
