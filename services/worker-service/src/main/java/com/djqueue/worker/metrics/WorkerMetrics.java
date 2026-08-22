package com.djqueue.worker.metrics;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Component;

@Component
public class WorkerMetrics {

    private final Counter success;
    private final Counter failure;
    private final Counter retry;

    public WorkerMetrics(MeterRegistry registry) {
        this.success = registry.counter("job_processed_total");
        this.failure = registry.counter("job_failed_total");
        this.retry = registry.counter("job_retry_total");
    }

    public void success() { success.increment(); }
    public void failure() { failure.increment(); }
    public void retry() { retry.increment(); }
}