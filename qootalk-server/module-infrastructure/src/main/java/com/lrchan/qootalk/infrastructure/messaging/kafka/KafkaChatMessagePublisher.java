package com.lrchan.qootalk.infrastructure.messaging.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.lrchan.qootalk.application.chat.dto.event.ChatMessageEvent;
import com.lrchan.qootalk.application.chat.port.out.PublishChatMessagePort;
import com.lrchan.qootalk.infrastructure.config.KafkaMessagingProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KafkaChatMessagePublisher implements PublishChatMessagePort {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaMessagingProperties kafkaMessagingProperties;

    @Override
    public void publish(ChatMessageEvent event) {
        kafkaTemplate.send(
            kafkaMessagingProperties.topics().chatMessage(),
            String.valueOf(event.roomId()),
            event
        );
    }
}
