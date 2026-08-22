package com.djqueue.retry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.djqueue")
@EnableScheduling
public class RetryApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetryApplication.class, args);
    }
}