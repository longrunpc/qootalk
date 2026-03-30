package com.lrchan.qootalk.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "messaging.kafka")
public record KafkaMessagingProperties(
    Topics topics,
    String consumerGroup,
    int partitionCount,
    short replicationFactor
) {

    public record Topics(
        String chatMessage,
        String readReceipt
    ) {
    }
}
