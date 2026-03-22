package com.lrchan.qootalk.infrastructure.messaging.kafka;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.lrchan.qootalk.application.chat.dto.event.ChatMessageEvent;
import com.lrchan.qootalk.application.chat.dto.event.UserChatMessageEvent;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.infrastructure.config.RedisPubSubConfig.MessagePublisher;
import com.lrchan.qootalk.infrastructure.messaging.presence.RedisUserPresenceTracker;

@Component
public class ChatMessageKafkaListener {

    private final LoadRoomParticipantPort loadRoomParticipantPort;
    private final RedisUserPresenceTracker redisUserPresenceTracker;
    private final MessagePublisher messagePublisher;
    private final ChannelTopic chatMessageChannelTopic;

    public ChatMessageKafkaListener(
        LoadRoomParticipantPort loadRoomParticipantPort,
        RedisUserPresenceTracker redisUserPresenceTracker,
        MessagePublisher messagePublisher,
        @Qualifier("chatMessageChannelTopic") ChannelTopic chatMessageChannelTopic
    ) {
        this.loadRoomParticipantPort = loadRoomParticipantPort;
        this.redisUserPresenceTracker = redisUserPresenceTracker;
        this.messagePublisher = messagePublisher;
        this.chatMessageChannelTopic = chatMessageChannelTopic;
    }

    @KafkaListener(
        topics = "${messaging.kafka.topics.chat-message:qootalk.chat.message}",
        groupId = "${messaging.kafka.consumer-group:qootalk-chat}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(ChatMessageEvent event) {
        loadRoomParticipantPort.findActiveByRoomId(event.roomId()).stream()
            .map(participant -> participant.userId())
            .filter(redisUserPresenceTracker::isConnected)
            .map(userId -> new UserChatMessageEvent(userId, event))
            .forEach(userEvent -> messagePublisher.publish(chatMessageChannelTopic, userEvent));
    }
}
