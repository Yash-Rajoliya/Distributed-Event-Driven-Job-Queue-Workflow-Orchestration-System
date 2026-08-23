package com.djqueue.dlq.infrastructure.kafka;

import com.djqueue.common.constants.KafkaTopics;
import com.djqueue.common.dto.v1.JobEventV1;
import com.djqueue.dlq.application.DLQService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DLQConsumer {

    private final DLQService dlqService;

    @KafkaListener(topics = KafkaTopics.DLQ_TOPIC, groupId = "dlq-group")
    public void consume(JobEventV1 event) {
        dlqService.handleFailure(event);
    }
}