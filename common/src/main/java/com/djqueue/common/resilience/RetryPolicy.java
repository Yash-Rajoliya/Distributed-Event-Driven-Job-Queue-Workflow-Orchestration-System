package com.djqueue.common.resilience;

import org.springframework.stereotype.Component;

@Component
public class RetryPolicy {

    public long getBackoff(int retryCount) {
        // Exponential backoff
        return (long) Math.min(1000 * Math.pow(2, retryCount), 30000);
    }
}