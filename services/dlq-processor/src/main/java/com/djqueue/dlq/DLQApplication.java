package com.djqueue.dlq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.djqueue")
public class DLQApplication {

    public static void main(String[] args) {
        SpringApplication.run(DLQApplication.class, args);
    }
}