package com.djqueue.retry.domain.model;

import java.time.Instant;

public class RetryJob {

    private String jobId;
    private int attempt;
    private Instant nextRetryTime;

    // getters setters
}