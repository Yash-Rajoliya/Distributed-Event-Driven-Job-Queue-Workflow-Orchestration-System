package com.djqueue.retry.infrastructure.kafka;

import com.djqueue.common.constants.KafkaTopics;
import com.djqueue.common.dto.v1.JobEventV1;
import com.djqueue.retry.application.RetryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RetryConsumer {

    private final RetryService retryService;

    @KafkaListener(topics = KafkaTopics.RETRY_TOPIC, groupId = "retry-group")
    public void consume(JobEventV1 event) {
        retryService.scheduleRetry(event);
    }
}