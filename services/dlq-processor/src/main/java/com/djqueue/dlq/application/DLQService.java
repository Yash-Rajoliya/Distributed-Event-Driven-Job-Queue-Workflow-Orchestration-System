package com.djqueue.dlq.application;

import com.djqueue.common.dto.v1.JobEventV1;
import com.djqueue.dlq.infrastructure.db.FailureLog;
import com.djqueue.dlq.repository.FailureLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DLQService {

    private final FailureLogRepository repository;

    public void handleFailure(JobEventV1 event) {
        FailureLog log = FailureLog.builder()
                .jobId(event.getJobId())
                .payload(event.getPayload())
                .retryCount(event.getRetryCount())
                .failedAt(System.currentTimeMillis())
                .build();

        repository.save(log);
    }
}