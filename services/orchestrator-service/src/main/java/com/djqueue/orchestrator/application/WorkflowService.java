package com.djqueue.orchestrator.application;

import com.djqueue.orchestrator.saga.JobSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final JobSagaOrchestrator orchestrator;

    public void startWorkflow(String jobId) {
        orchestrator.startSaga(jobId);
    }
}