package com.lrchan.qootalk.infrastructure;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.lrchan.qootalk")
@EnableJpaRepositories(basePackages = "com.lrchan.qootalk")
public class InfrastructureTestApplication {
}
