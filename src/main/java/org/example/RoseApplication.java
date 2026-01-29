package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing  // Enable auditing for @CreatedDate and @LastModifiedDate
public class RoseApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoseApplication.class, args);
    }
}