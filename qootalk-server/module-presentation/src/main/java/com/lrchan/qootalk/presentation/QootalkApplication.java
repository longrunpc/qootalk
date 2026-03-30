package com.lrchan.qootalk.presentation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.lrchan.qootalk")
@EnableJpaRepositories(basePackages = "com.lrchan.qootalk")
@EntityScan(basePackages = "com.lrchan.qootalk")
public class QootalkApplication {

	public static void main(String[] args) {
		SpringApplication.run(QootalkApplication.class, args);
	}

}