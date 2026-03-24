package com.lrchan.qootalk.infrastructure.config;

import java.time.Duration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import com.lrchan.qootalk.infrastructure.messaging.redis.MessagePublisher;
import com.lrchan.qootalk.infrastructure.messaging.redis.RedisMessagePublisher;

@Configuration
@EnableConfigurationProperties(RedisMessagingProperties.class)
public class RedisPubSubConfig {

    @Bean
    ChannelTopic chatMessageChannelTopic(RedisMessagingProperties properties) {
        return new ChannelTopic(properties.channels().chatMessage());
    }

    @Bean
    ChannelTopic userPresenceChannelTopic(RedisMessagingProperties properties) {
        return new ChannelTopic(properties.channels().userPresence());
    }

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    @Bean
    Duration userPresenceTtl(RedisMessagingProperties properties) {
        return Duration.ofSeconds(properties.presenceTtlSeconds());
    }

    @Bean
    MessagePublisher redisMessagePublisher(RedisTemplate<String, Object> redisTemplate) {
        return new RedisMessagePublisher(redisTemplate);
    }
}
