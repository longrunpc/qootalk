package com.lrchan.qootalk.infrastructure;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
public abstract class IntegrationTestSupport {

    static final PostgreSQLContainer<?> postgres;
    static final LocalStackContainer localstack;

    static {
        postgres = new PostgreSQLContainer<>("postgres:17-alpine");
        localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
                .withServices(LocalStackContainer.Service.S3);

        postgres.start();
        localstack.start();
        
        // 
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
        registry.add("aws.s3.endpoint", () -> localstack.getEndpointOverride(LocalStackContainer.Service.S3).toString());
        registry.add("aws.s3.bucket.name", () -> "qootalk-s3-local");
        registry.add("aws.s3.region", () -> "ap-northeast-2");
        registry.add("aws.s3.access-key", () -> "accessKey");
        registry.add("aws.s3.secret-key", () -> "secretKey");
    }
}