package com.lrchan.qootalk.presentation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.lrchan.qootalk")
public class QootalkApplication {

	public static void main(String[] args) {
		SpringApplication.run(QootalkApplication.class, args);
	}

}

