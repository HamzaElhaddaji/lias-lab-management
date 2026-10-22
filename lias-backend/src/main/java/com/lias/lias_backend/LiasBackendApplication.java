package com.lias.lias_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@org.springframework.scheduling.annotation.EnableScheduling
@EnableJpaAuditing
public class LiasBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(LiasBackendApplication.class, args);
    }
}