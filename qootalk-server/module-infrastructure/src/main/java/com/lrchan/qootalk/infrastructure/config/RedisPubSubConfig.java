package com.lrchan.qootalk.infrastructure.config;

import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(RedisMessagingProperties.class)
public class RedisPubSubConfig {

    @Value("${qootalk.channels.chat-message}")
    private String chatMessageChannel;

    @Value("${qootalk.channels.user-presence}")
    private String userPresenceChannel;

    @Value("${qootalk.presence-ttl-seconds}")
    private long presenceTtlSeconds;

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
    MessagePublisher redisMessagePublisher(RedisTemplate<String, Object> redisTemplate) {
        return new RedisMessagePublisher(redisTemplate);
    }

    public interface MessagePublisher {
        void publish(ChannelTopic topic, Object payload);
    }

    static class RedisMessagePublisher implements MessagePublisher {
        private final RedisTemplate<String, Object> redisTemplate;

        RedisMessagePublisher(RedisTemplate<String, Object> redisTemplate) {
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
            .orElse(chatMessageChannel);
    }

    private String resolveUserPresenceChannel(RedisMessagingProperties properties) {
        return Optional.ofNullable(properties)
            .map(RedisMessagingProperties::channels)
            .map(RedisMessagingProperties.Channels::userPresence)
            .filter(StringUtils::hasText)
            .orElse(userPresenceChannel);
    }

    private long resolvePresenceTtlSeconds(RedisMessagingProperties properties) {
        return Optional.ofNullable(properties)
            .map(RedisMessagingProperties::presenceTtlSeconds)
            .filter(ttl -> ttl > 0)
            .orElse(presenceTtlSeconds);
    }
}
