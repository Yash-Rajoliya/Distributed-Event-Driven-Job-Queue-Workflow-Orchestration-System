package com.djqueue.common.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.*;

@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistry meterRegistry() {
        return io.micrometer.core.instrument.simple.SimpleMeterRegistry.builder().build();
    }
}