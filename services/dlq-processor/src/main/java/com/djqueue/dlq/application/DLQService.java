package com.djqueue.dlq.application;

import com.djqueue.common.dto.v1.JobEventV1;
import com.djqueue.dlq.infrastructure.db.FailureLog;
import com.djqueue.dlq.repository.FailureLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DLQService {

    private final FailureLogRepository repository;

    @Transactional
    public void handleFailure(JobEventV1 event) {
        if (event == null) {
            log.error("Received null JobEventV1 in DLQ service");
            return;
        }

        // Prevent infinite retry loops from flooding the DLQ table with duplicate logs
        if (repository.existsByJobIdAndRetryCount(event.getJobId(), event.getRetryCount())) {
            log.warn("Failure log already recorded for jobId: {} with retryCount: {}. Skipping duplicate.",
                    event.getJobId(), event.getRetryCount());
            return;
        }

        try {
            FailureLog failureLog = FailureLog.builder()
                    .jobId(event.getJobId())
                    .payload(event.getPayload())
                    .retryCount(event.getRetryCount())
                    .failedAt(System.currentTimeMillis())
                    .build();

            repository.save(failureLog);
            log.info("Successfully persisted failure log for jobId: {}", event.getJobId());
        } catch (Exception e) {
            log.error("Failed to persist failure log for jobId: {}", event.getJobId(), e);
            throw e;
        }
    }
}