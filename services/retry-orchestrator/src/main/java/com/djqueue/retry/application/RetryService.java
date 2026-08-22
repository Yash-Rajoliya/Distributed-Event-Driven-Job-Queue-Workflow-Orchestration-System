package com.djqueue.retry.application;

import com.djqueue.common.constants.KafkaTopics;
import com.djqueue.common.dto.v1.JobEventV1;
import com.djqueue.common.resilience.RetryPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RetryService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RetryPolicy retryPolicy;

    public void scheduleRetry(JobEventV1 event) {
        long delay = retryPolicy.getBackoff(event.getRetryCount());

        try {
            Thread.sleep(delay); // simulate delay (real-world → delay queues / Kafka delay topics)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        kafkaTemplate.send(KafkaTopics.JOB_TOPIC, event.getJobId(), event);
    }
}