package com.banque.msoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MsOcApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsOcApplication.class, args);
    }
}
