package com.lrchan.qootalk.infrastructure;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import com.redis.testcontainers.RedisContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
public abstract class IntegrationTestSupport {

    static final PostgreSQLContainer postgres;
    static final RedisContainer redis;
    static final KafkaContainer kafka;
    static final LocalStackContainer localstack;

    static {
        postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine"));
        localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.0.0"))
            .withEnv("DEFAULT_REGION", "ap-northeast-2");
        redis = new RedisContainer(DockerImageName.parse("redis:7-alpine"));
        kafka = new KafkaContainer(
            DockerImageName.parse("apache/kafka-native:3.8.0")
                .asCompatibleSubstituteFor("apache/kafka")
        );

        redis.start();
        kafka.start();
        postgres.start();
        localstack.start();
        
        try {
            localstack.execInContainer("awslocal", "s3", "mb", "s3://qootalk-s3-local");
        } catch (Exception e) { /* ignore */ }
    }


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // --- DB 설정 ---
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        
        // Flyway & JPA 설정
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
        registry.add("spring.flyway.clean-disabled", () -> "false");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");

        // --- S3 (LocalStack) 설정 ---
        registry.add("aws.s3.endpoint", () -> localstack.getEndpoint().toString());
        registry.add("aws.s3.bucket.name", () -> "qootalk-s3-local");
        registry.add("aws.s3.region", () -> "ap-northeast-2");
        registry.add("aws.s3.access-key", () -> "accessKey");
        registry.add("aws.s3.secret-key", () -> "secretKey");

        // --- Redis (Testcontainers) 설정 ---
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        // --- JWT 설정 ---
        // 이 값은 테스트 코드에서 사용되는 임시 값이므로, 실제 개발 환경에서는 사용하지 않습니다.
        registry.add("jwt.secret", () -> "BZ8uxA8V9Iz//+hgMZ9j+TZBWNfytwMoJU3QkkcT/aQ=");
        registry.add("jwt.access-expiration", () -> "86400000");
        registry.add("jwt.refresh-expiration", () -> "604800000");

        // --- Redis PubSub 설정 ---
        registry.add("messaging.redis.channels.chat-message", () -> "qootalk:chat:message");
        registry.add("messaging.redis.channels.user-presence", () -> "qootalk:user:presence");
        registry.add("messaging.redis.presence-ttl-seconds", () -> "300");

        // --- Kafka 설정 ---
        registry.add("messaging.kafka.topics.chat-message", () -> "qootalk.chat.message");
        registry.add("messaging.kafka.consumer-group", () -> "qootalk-chat");
    }
}
