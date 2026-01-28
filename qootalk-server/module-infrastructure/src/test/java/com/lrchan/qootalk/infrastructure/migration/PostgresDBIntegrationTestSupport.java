package com.lrchan.qootalk.infrastructure.migration;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;

@ActiveProfiles("test")
@DataJpaTest
@Testcontainers
public abstract class PostgresDBIntegrationTestSupport {

    // 정적 필드로 선언하여 클래스 간 컨테이너 공유
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("qootalk")
            .withUsername("sa")
            .withPassword("password");

    static {
        postgres.start(); // 컨테이너 수동 시작
    }

    // 컨테이너에서 할당된 동적 포트 정보를 스프링 설정에 주입
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        // 마이그레이션 테스트를 위해 Flyway 활성화
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
        registry.add("spring.flyway.clean-disabled", () -> "false");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }
}