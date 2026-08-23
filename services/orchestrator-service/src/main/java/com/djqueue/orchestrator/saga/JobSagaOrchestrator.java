package com.djqueue.orchestrator.saga;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobSagaOrchestrator {

    private final CompensationHandler compensationHandler;
    private final List<SagaStep> steps;

    public void startSaga(String jobId) {
        // Ensure steps execute in strictly defined order
        List<SagaStep> orderedSteps = new ArrayList<>(steps);
        OrderComparator.sort(orderedSteps);

        List<SagaStep> completedSteps = new ArrayList<>();

        try {
            for (SagaStep step : orderedSteps) {
                log.info("Executing step {} for job: {}", step.getName(), jobId);
                step.execute(jobId);
                completedSteps.add(step);
            }

            log.info("Saga completed successfully for job: {}", jobId);

        } catch (Exception e) {
            log.error("Saga failed at step for job: {}. Triggering reverse compensation...", jobId, e);
            // Reverse step order so compensation executes in LIFO order
            Collections.reverse(completedSteps);
            compensationHandler.compensate(completedSteps, jobId);
        }
    }
}