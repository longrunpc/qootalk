package com.lrchan.qootalk.infrastructure.messaging.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.lrchan.qootalk.application.chat.dto.event.UserChatMessageEvent;
import com.lrchan.qootalk.infrastructure.messaging.presence.LocalUserConnectionRegistry;

@Component
public class RedisChatMessageSubscriber implements MessageListener, InitializingBean {

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final ChannelTopic chatMessageChannelTopic;
    private final RedisTemplate<String, Object> redisTemplate;
    private final LocalUserConnectionRegistry localUserConnectionRegistry;

    public RedisChatMessageSubscriber(
        RedisMessageListenerContainer redisMessageListenerContainer,
        @Qualifier("chatMessageChannelTopic") ChannelTopic chatMessageChannelTopic,
        RedisTemplate<String, Object> redisTemplate,
        LocalUserConnectionRegistry localUserConnectionRegistry
    ) {
        this.redisMessageListenerContainer = redisMessageListenerContainer;
        this.chatMessageChannelTopic = chatMessageChannelTopic;
        this.redisTemplate = redisTemplate;
        this.localUserConnectionRegistry = localUserConnectionRegistry;
    }

    @Override
    public void afterPropertiesSet() {
        redisMessageListenerContainer.addMessageListener(this, chatMessageChannelTopic);
    }

    @Override
    public void onMessage(org.springframework.data.redis.connection.Message message, byte[] pattern) {
        RedisSerializer<?> serializer = redisTemplate.getValueSerializer();
        Object payload = serializer.deserialize(message.getBody());
        if (!(payload instanceof UserChatMessageEvent userEvent)) {
            return;
        }
        localUserConnectionRegistry.dispatch(userEvent.recipientId(), userEvent.message());
    }
}
