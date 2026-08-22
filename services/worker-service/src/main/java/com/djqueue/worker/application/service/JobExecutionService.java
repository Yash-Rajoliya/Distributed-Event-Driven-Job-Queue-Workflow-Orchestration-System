package com.djqueue.worker.application.service;

import com.djqueue.worker.domain.handler.JobHandler;
import com.djqueue.worker.domain.model.Job;
import com.djqueue.worker.infrastructure.redis.IdempotencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobExecutionService {

    private final List<JobHandler> handlers;
    private final IdempotencyRepository idempotency;
    private final RetryService retryService;

    public void execute(Job job, int retryCount) {
        // Atomically check and acquire lock to eliminate race condition
        if (!idempotency.trySetProcessed(job.getId())) {
            log.info("Job {} is already being processed or completed.", job.getId());
            return;
        }

        try {
            for (JobHandler handler : handlers) {
                if (handler.supports(job.getPayload())) {
                    handler.handle(job);
                    return;
                }
            }
        } catch (Exception e) {
            log.error("Failed to execute job {}", job.getId(), e);
            // Release lock or handle retry scheduling safely
            idempotency.remove(job.getId());
            retryService.scheduleRetry(job.getId(), job, retryCount);
        }
    }
}