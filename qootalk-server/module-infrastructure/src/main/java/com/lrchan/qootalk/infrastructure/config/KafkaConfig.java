package com.lrchan.qootalk.infrastructure.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(KafkaMessagingProperties.class)
public class KafkaConfig {

    @Bean
    KafkaAdmin.NewTopics chatMessageTopic(KafkaMessagingProperties properties) {
        return new KafkaAdmin.NewTopics(
            new NewTopic(resolveTopic(properties.topics().chatMessage()), 3, (short) 1),
            new NewTopic(resolveTopic(properties.topics().readReceipt()), 3, (short) 1)
        );
    }

    @Bean
    ProducerFactory<String, Object> kafkaProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> properties = new HashMap<>(kafkaProperties.buildProducerProperties());
        properties.putIfAbsent(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.putIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    ConsumerFactory<String, Object> kafkaConsumerFactory(
        KafkaProperties kafkaProperties,
        KafkaMessagingProperties messagingProperties
    ) {
        Map<String, Object> properties = new HashMap<>(kafkaProperties.buildConsumerProperties());
        properties.putIfAbsent(ConsumerConfig.GROUP_ID_CONFIG, resolveConsumerGroup(messagingProperties));
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
        ConsumerFactory<String, Object> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    private String resolveTopic(String topic) {
        return Optional.ofNullable(topic)
            .filter(StringUtils::hasText)
            .orElseThrow(() -> new IllegalStateException("Kafka topic must not be blank"));
    }

    private String resolveConsumerGroup(KafkaMessagingProperties properties) {
        return Optional.ofNullable(properties)
            .map(KafkaMessagingProperties::consumerGroup)
            .filter(StringUtils::hasText)
            .orElse(properties.consumerGroup());
    }
}
