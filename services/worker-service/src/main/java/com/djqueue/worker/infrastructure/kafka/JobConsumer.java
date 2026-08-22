package com.djqueue.worker.infrastructure.kafka;

import com.djqueue.common.constants.KafkaTopics;
import com.djqueue.common.dto.v1.JobEventV1;
import com.djqueue.worker.application.service.JobExecutionService;
import com.djqueue.worker.application.service.RetryService;
import com.djqueue.worker.domain.model.Job;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobConsumer {

    private final JobExecutionService executionService;
    private final RetryService retryService;

    private static final int MAX_RETRIES = 3;

    @KafkaListener(topics = KafkaTopics.JOB_TOPIC, groupId = "worker-group")
    public void consume(JobEventV1 event) {

        Job job = Job.builder()
                .id(event.getJobId())
                .payload(event.getPayload())
                .retryCount(event.getRetryCount())
                .build();

        try {
            executionService.execute(job);
        } catch (Exception e) {

            if (event.getRetryCount() >= MAX_RETRIES) {
                retryService.sendToDLQ(event);
            } else {
                retryService.retry(event);
            }
        }
    }
}