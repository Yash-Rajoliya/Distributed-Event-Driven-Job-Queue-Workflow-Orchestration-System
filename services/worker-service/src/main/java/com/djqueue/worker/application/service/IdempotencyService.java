package com.djqueue.worker.application.service;

import com.djqueue.worker.infrastructure.redis.IdempotencyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class IdempotencyService {

    private final IdempotencyRepository repository;
    private static final Duration LOCK_TTL = Duration.ofMinutes(10);

    public IdempotencyService(IdempotencyRepository repository) {
        this.repository = repository;
    }

    /**
     * Atomically attempts to acquire execution rights for the given jobId.
     * Prevents collision when multiple consumers process the same key concurrently.
     *
     * @param jobId unique job identifier
     * @return true if the job is duplicate or locked by another worker; false if key was successfully acquired
     */
    public boolean isDuplicate(String jobId) {
        boolean acquired = repository.saveIfAbsent(jobId, "IN_PROGRESS", LOCK_TTL);
        return !acquired;
    }

    public void markProcessed(String jobId) {
        repository.save(jobId, "COMPLETED", LOCK_TTL);
    }

    public void releaseKey(String jobId) {
        repository.delete(jobId);
    }
}