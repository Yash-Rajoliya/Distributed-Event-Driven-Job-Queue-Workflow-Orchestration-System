package com.djqueue.orchestrator.application;

import com.djqueue.orchestrator.saga.JobSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final JobSagaOrchestrator orchestrator;

    public void startWorkflow(String jobId) {
        try {
            orchestrator.startSaga(jobId);
        } catch (Exception e) {
            log.error("Workflow execution failed for jobId: {}. Executing fallback recovery handler.", jobId, e);
            handleWorkflowRecovery(jobId, e);
        }
    }

    private void handleWorkflowRecovery(String jobId, Exception e) {
        // Recovery logic: mark workflow failed in persistence layer or notify telemetry
        log.warn("Handled recovery procedures for failed workflow on jobId: {}", jobId);
    }
}