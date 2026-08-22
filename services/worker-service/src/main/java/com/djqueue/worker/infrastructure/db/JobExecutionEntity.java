package com.djqueue.worker.infrastructure.db;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "job_execution")
public class JobExecutionEntity {

    @Id
    private String jobId;

    private String status;

    private Instant processedAt;

    public JobExecutionEntity() {}

    public JobExecutionEntity(String jobId, String status) {
        this.jobId = jobId;
        this.status = status;
        this.processedAt = Instant.now();
    }

    // getters setters
}