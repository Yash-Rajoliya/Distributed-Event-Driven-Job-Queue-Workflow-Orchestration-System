package com.djqueue.worker.application.service;

import com.djqueue.common.constants.KafkaTopics;
import com.djqueue.common.dto.v1.JobEventV1;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RetryService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void retry(JobEventV1 event) {
        event.setRetryCount(event.getRetryCount() + 1);
        kafkaTemplate.send(KafkaTopics.RETRY_TOPIC, event.getJobId(), event);
    }

    public void sendToDLQ(JobEventV1 event) {
        kafkaTemplate.send(KafkaTopics.DLQ_TOPIC, event.getJobId(), event);
    }
}