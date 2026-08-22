package com.djqueue.worker.application.service;

import com.djqueue.worker.infrastructure.redis.IdempotencyRepository;
import org.springframework.stereotype.Service;

@Service
public class IdempotencyService {

    private final IdempotencyRepository repository;

    public IdempotencyService(IdempotencyRepository repository) {
        this.repository = repository;
    }

    public boolean isDuplicate(String jobId) {
        return repository.exists(jobId);
    }

    public void markProcessed(String jobId) {
        repository.save(jobId);
    }
}