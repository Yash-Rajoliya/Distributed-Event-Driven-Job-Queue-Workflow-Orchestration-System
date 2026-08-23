package com.djqueue.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobMetrics {

    private final MeterRegistry meterRegistry;

    public void incrementProcessed(String queueName, String status) {
        Counter.builder("jobs.processed")
                .description("Total count of processed jobs")
                .tag("queue", queueName != null ? queueName : "unknown")
                .tag("status", status != null ? status : "unknown")
                .register(meterRegistry)
                .increment();
    }

    public void incrementFailed(String queueName, String exceptionType) {
        Counter.builder("jobs.failed")
                .description("Total count of failed job processing attempts")
                .tag("queue", queueName != null ? queueName : "unknown")
                .tag("exception", exceptionType != null ? exceptionType : "none")
                .register(meterRegistry)
                .increment();
    }
}