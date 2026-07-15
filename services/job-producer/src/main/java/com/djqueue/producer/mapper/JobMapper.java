package com.djqueue.producer.mapper;

import com.djqueue.producer.domain.model.Job;
import com.djqueue.producer.infrastructure.db.JobEntity;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {

    public JobEntity toEntity(Job job) {
        return JobEntity.builder()
                .id(job.getId())
                .payload(job.getPayload())
                .status(job.getStatus())
                .createdAt(job.getCreatedAt())
                .build();
    }

    public Job toDomain(JobEntity entity) {
        return Job.builder()
                .id(entity.getId())
                .payload(entity.getPayload())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}