package com.djqueue.common.resilience;

import io.github.resilience4j.circuitbreaker.*;
import org.springframework.context.annotation.*;

@Configuration
public class CircuitBreakerConfig {

    @Bean
    public CircuitBreaker jobCircuitBreaker() {

        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(java.time.Duration.ofSeconds(10))
                .build();

        return CircuitBreaker.of("jobProcessor", config);
    }
}