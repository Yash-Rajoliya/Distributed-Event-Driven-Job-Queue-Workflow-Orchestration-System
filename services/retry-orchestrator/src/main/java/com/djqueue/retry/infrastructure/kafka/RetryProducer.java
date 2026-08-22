package com.djqueue.retry.infrastructure.kafka;

import com.djqueue.retry.domain.RetryJob;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RetryProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void republish(RetryJob job) {
        kafkaTemplate.send("job-topic", job.getPayload());
    }
}