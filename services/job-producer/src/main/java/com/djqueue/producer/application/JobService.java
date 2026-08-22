package com.djqueue.producer.application;

import com.djqueue.common.constants.KafkaTopics;
import com.djqueue.common.dto.v1.JobEventV1;
import com.djqueue.common.utils.IdGenerator;
import com.djqueue.common.utils.TimeUtil;
import com.djqueue.producer.domain.model.Job;
import com.djqueue.producer.domain.repository.JobRepository;
import com.djqueue.producer.infrastructure.kafka.JobProducer;
import com.djqueue.producer.mapper.JobMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository repository;
    private final JobProducer kafkaProducer;
    private final JobMapper mapper;

    @Transactional
    public String createJob(String payload) {

        String jobId = IdGenerator.generate();

        Job job = Job.builder()
                .id(jobId)
                .payload(payload)
                .status("CREATED")
                .createdAt(TimeUtil.now())
                .build();

        repository.save(job);

        JobEventV1 event = JobEventV1.builder()
                .jobId(jobId)
                .payload(payload)
                .retryCount(0)
                .createdAt(TimeUtil.now())
                .build();

        kafkaProducer.publish(KafkaTopics.JOB_TOPIC, jobId, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event for jobId: {}", jobId, ex);
                        job.setStatus("FAILED");
                        repository.save(job);
                    } else {
                        log.info("Successfully published event for jobId: {}", jobId);
                        job.setStatus("PUBLISHED");
                        repository.save(job);
                    }
                });

        return jobId;
    }
}