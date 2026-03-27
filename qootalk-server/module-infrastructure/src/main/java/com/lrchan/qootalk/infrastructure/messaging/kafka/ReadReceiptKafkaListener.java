package com.lrchan.qootalk.infrastructure.messaging.kafka;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.lrchan.qootalk.application.chat.dto.event.ReadReceiptEvent;
import com.lrchan.qootalk.application.chat.dto.event.UserReadReceiptEvent;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.infrastructure.messaging.presence.RedisUserPresenceTracker;
import com.lrchan.qootalk.infrastructure.messaging.redis.MessagePublisher;

@Component
public class ReadReceiptKafkaListener {

    private final LoadRoomParticipantPort loadRoomParticipantPort;
    private final RedisUserPresenceTracker redisUserPresenceTracker;
    private final MessagePublisher messagePublisher;
    private final ChannelTopic readReceiptChannelTopic;

    public ReadReceiptKafkaListener(
        LoadRoomParticipantPort loadRoomParticipantPort,
        RedisUserPresenceTracker redisUserPresenceTracker,
        MessagePublisher messagePublisher,
        @Qualifier("readReceiptChannelTopic") ChannelTopic readReceiptChannelTopic
    ) {
        this.loadRoomParticipantPort = loadRoomParticipantPort;
        this.redisUserPresenceTracker = redisUserPresenceTracker;
        this.messagePublisher = messagePublisher;
        this.readReceiptChannelTopic = readReceiptChannelTopic;
    }

    @KafkaListener(
        topics = "${messaging.kafka.topics.read-receipt:qootalk.read.receipt}",
        groupId = "${messaging.kafka.consumer-group:qootalk-chat}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(ReadReceiptEvent event) {
        loadRoomParticipantPort.findActiveByRoomId(event.roomId()).stream()
            .map(participant -> participant.userId())
            .filter(userId -> !userId.equals(event.readerId()))
            .filter(redisUserPresenceTracker::isConnected)
            .map(userId -> new UserReadReceiptEvent(userId, event))
            .forEach(userEvent -> messagePublisher.publish(readReceiptChannelTopic, userEvent));
    }
}
