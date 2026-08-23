package com.djqueue.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JobMetrics {

    private final MeterRegistry meterRegistry;
    private final Map<String, Counter> counterCache = new ConcurrentHashMap<>();

    public JobMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void incrementProcessed(String queueName, String status) {
        String sanitizedQueue = sanitizeTagValue(queueName);
        String sanitizedStatus = sanitizeTagValue(status);
        
        String key = "processed:" + sanitizedQueue + ":" + sanitizedStatus;
        counterCache.computeIfAbsent(key, k -> 
            Counter.builder("jobs.processed")
                    .description("Total count of processed jobs")
                    .tag("queue", sanitizedQueue)
                    .tag("status", sanitizedStatus)
                    .register(meterRegistry)
        ).increment();
    }

    public void incrementFailed(String queueName, String exceptionType) {
        String sanitizedQueue = sanitizeTagValue(queueName);
        String generalizedException = categorizeException(exceptionType);

        String key = "failed:" + sanitizedQueue + ":" + generalizedException;
        counterCache.computeIfAbsent(key, k -> 
            Counter.builder("jobs.failed")
                    .description("Total count of failed job processing attempts")
                    .tag("queue", sanitizedQueue)
                    .tag("exception", generalizedException)
                    .register(meterRegistry)
        ).increment();
    }

    private String sanitizeTagValue(String input) {
        if (input == null || input.isBlank()) {
            return "unknown";
        }
        // Restrict length and strip out dynamic parameters/IDs
        return input.replaceAll("[^a-zA-Z0-9_-]", "_").toLowerCase();
    }

    private String categorizeException(String exceptionType) {
        if (exceptionType == null || exceptionType.isBlank()) {
            return "none";
        }
        // Extract simple class name or generic type to keep cardinality low
        int lastDot = exceptionType.lastIndexOf('.');
        String simpleName = (lastDot != -1) ? exceptionType.substring(lastDot + 1) : exceptionType;
        return sanitizeTagValue(simpleName);
    }
}