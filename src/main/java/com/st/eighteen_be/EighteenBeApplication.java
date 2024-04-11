package com.st.eighteen_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class EighteenBeApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(EighteenBeApplication.class, args);
    }
}