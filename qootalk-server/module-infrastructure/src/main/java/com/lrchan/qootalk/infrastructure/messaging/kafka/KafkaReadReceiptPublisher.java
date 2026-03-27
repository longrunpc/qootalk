package com.lrchan.qootalk.infrastructure.messaging.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.lrchan.qootalk.application.chat.dto.event.ReadReceiptEvent;
import com.lrchan.qootalk.application.chat.port.out.PublishReadReceiptPort;
import com.lrchan.qootalk.infrastructure.config.KafkaMessagingProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KafkaReadReceiptPublisher implements PublishReadReceiptPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaMessagingProperties kafkaMessagingProperties;

    @Override
    public void publish(ReadReceiptEvent event) {
        kafkaTemplate.send(
            kafkaMessagingProperties.topics().readReceipt(),
            String.valueOf(event.roomId()),
            event
        );
    }
}
