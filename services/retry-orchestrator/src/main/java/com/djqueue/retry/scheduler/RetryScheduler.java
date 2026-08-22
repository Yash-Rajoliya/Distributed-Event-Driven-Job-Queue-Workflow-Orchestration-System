package com.djqueue.retry.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryScheduler {

    private final RetryLockRepository lockRepository;

    @Scheduled(fixedRate = 60000)
    public void monitorRetries() {
        String lockKey = "retry:scheduler:lock";
        
        // Ensure only one scheduler instance executes retry processing at a time
        if (!lockRepository.tryLock(lockKey, 55000)) {
            log.debug("Another scheduler instance is currently processing retries. Skipping execution.");
            return;
        }

        try {
            log.info("Acquired scheduler lock. Monitoring retry queue health and dispatching pending retries...");
            // Job retry polling and deduplicated scheduling logic
        } finally {
            lockRepository.releaseLock(lockKey);
        }
    }
}