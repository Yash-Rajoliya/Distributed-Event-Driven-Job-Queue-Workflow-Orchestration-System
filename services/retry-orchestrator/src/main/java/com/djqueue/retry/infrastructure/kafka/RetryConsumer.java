package com.djqueue.retry.infrastructure.kafka;

import com.djqueue.common.constants.KafkaTopics;
import com.djqueue.common.dto.v1.JobEventV1;
import com.djqueue.retry.application.RetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryConsumer {

    private final RetryService retryService;

    @KafkaListener(topics = KafkaTopics.RETRY_TOPIC, groupId = "retry-group")
    public void consume(ConsumerRecord<String, Object> record, @Payload(required = false) JobEventV1 event) {
        if (event == null) {
            log.error("Deserialization failed for message at partition {} offset {}. Raw key: {}",
                    record.partition(), record.offset(), record.key());
            // Route to DLQ or handle toxic payload to avoid consumer loop block
            return;
        }

        try {
            retryService.scheduleRetry(event);
        } catch (Exception e) {
            log.error("Error processing retry event for jobId: {}", event.getJobId(), e);
        }
    }
}