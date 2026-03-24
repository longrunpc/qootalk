package com.lrchan.qootalk.infrastructure.messaging.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RedisMessagePublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(ChannelTopic topic, Object payload) {
        redisTemplate.convertAndSend(topic.getTopic(), payload);
    }
}