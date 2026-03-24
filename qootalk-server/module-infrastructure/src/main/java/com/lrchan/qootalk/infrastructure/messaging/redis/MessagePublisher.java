package com.lrchan.qootalk.infrastructure.messaging.redis;

import org.springframework.data.redis.listener.ChannelTopic;

public interface MessagePublisher {
    void publish(ChannelTopic topic, Object payload);
}
