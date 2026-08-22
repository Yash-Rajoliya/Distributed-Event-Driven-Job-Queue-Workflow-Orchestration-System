package com.djqueue.worker.application.service;

import com.djqueue.worker.domain.handler.JobHandler;
import com.djqueue.worker.domain.model.Job;
import com.djqueue.worker.infrastructure.redis.IdempotencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobExecutionService {

    private final List<JobHandler> handlers;
    private final IdempotencyRepository idempotency;

    public void execute(Job job) {

        if (idempotency.isProcessed(job.getId())) {
            return;
        }

        for (JobHandler handler : handlers) {
            if (handler.supports(job.getPayload())) {
                handler.handle(job);
                idempotency.markProcessed(job.getId());
                return;
            }
        }

        catch (Exception e) {
    retryService.scheduleRetry(job.getId(), job, retryCount);
}
    }
}