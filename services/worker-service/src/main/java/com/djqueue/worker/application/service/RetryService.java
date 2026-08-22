package com.djqueue.worker.application.service;

import com.djqueue.common.constants.KafkaTopics;
import com.djqueue.common.dto.v1.JobEventV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetryService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final long INITIAL_INTERVAL_MS = 1000L; // 1 second base delay
    private static final double MULTIPLIER = 2.0;
    private static final long MAX_INTERVAL_MS = 30000L; // 30 seconds max delay

    public void retry(JobEventV1 event) {
        int nextRetryCount = event.getRetryCount() + 1;
        event.setRetryCount(nextRetryCount);

        long backoffDelay = calculateBackoffDelay(nextRetryCount);
        log.info("Scheduling retry #{} for jobId: {} with backoff delay: {}ms", nextRetryCount, event.getJobId(), backoffDelay);

        try {
            Thread.sleep(backoffDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Backoff delay interrupted for jobId: {}", event.getJobId());
        }

        kafkaTemplate.send(KafkaTopics.RETRY_TOPIC, event.getJobId(), event);
    }

    public void sendToDLQ(JobEventV1 event) {
        kafkaTemplate.send(KafkaTopics.DLQ_TOPIC, event.getJobId(), event);
    }

    private long calculateBackoffDelay(int retryCount) {
        long delay = (long) (INITIAL_INTERVAL_MS * Math.pow(MULTIPLIER, retryCount - 1));
        return Math.min(delay, MAX_INTERVAL_MS);
    }
}