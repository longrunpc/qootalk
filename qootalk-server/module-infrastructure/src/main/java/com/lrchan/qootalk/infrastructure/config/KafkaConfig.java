package com.lrchan.qootalk.infrastructure.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
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
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableConfigurationProperties(KafkaMessagingProperties.class)
public class KafkaConfig {

    private static final String DEFAULT_CHAT_MESSAGE_TOPIC = "qootalk.chat.message";
    private static final String DEFAULT_CONSUMER_GROUP = "qootalk-chat";

    @Bean
    KafkaAdmin.NewTopics chatMessageTopic(KafkaMessagingProperties properties) {
        return new KafkaAdmin.NewTopics(
            new NewTopic(resolveChatMessageTopic(properties), 3, (short) 1)
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
        KafkaMessagingProperties messagingProperties,
        ObjectMapper objectMapper
    ) {
        Map<String, Object> properties = new HashMap<>(kafkaProperties.buildConsumerProperties());
        properties.putIfAbsent(ConsumerConfig.GROUP_ID_CONFIG, resolveConsumerGroup(messagingProperties));
        properties.putIfAbsent(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        JsonDeserializer<Object> valueDeserializer = new JsonDeserializer<>(Object.class, objectMapper, false);
        valueDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
            properties,
            new StringDeserializer(),
            valueDeserializer
        );
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

    private String resolveChatMessageTopic(KafkaMessagingProperties properties) {
        return Optional.ofNullable(properties)
            .map(KafkaMessagingProperties::topics)
            .map(KafkaMessagingProperties.Topics::chatMessage)
            .filter(StringUtils::hasText)
            .orElse(DEFAULT_CHAT_MESSAGE_TOPIC);
    }

    private String resolveConsumerGroup(KafkaMessagingProperties properties) {
        return Optional.ofNullable(properties)
            .map(KafkaMessagingProperties::consumerGroup)
            .filter(StringUtils::hasText)
            .orElse(DEFAULT_CONSUMER_GROUP);
    }
}
