package com.djqueue.orchestrator.saga;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobSagaOrchestrator {

    private final CompensationHandler compensationHandler;
    private final List<SagaStep> steps;

    public void startSaga(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            throw new IllegalArgumentException("Job ID cannot be null or empty");
        }

        if (steps == null || steps.isEmpty()) {
            log.warn("No saga steps configured for job: {}", jobId);
            return;
        }

        List<SagaStep> completedSteps = new ArrayList<>();

        try {
            for (SagaStep step : steps) {
                log.info("Executing step [{}] for job: {}", step.getName(), jobId);
                step.execute(jobId);
                completedSteps.add(step);
            }

            log.info("Saga completed successfully for job: {}", jobId);

        } catch (Exception e) {
            log.error("Saga execution failed for job: {}. Triggering compensation steps...", jobId, e);
            
            try {
                compensationHandler.compensate(completedSteps, jobId);
            } catch (Exception compEx) {
                log.error("Compensation failed for job: {}. Critical recovery intervention required.", jobId, compEx);
            }

            throw new RuntimeException("Saga failed for job: " + jobId, e);
        }
    }
}