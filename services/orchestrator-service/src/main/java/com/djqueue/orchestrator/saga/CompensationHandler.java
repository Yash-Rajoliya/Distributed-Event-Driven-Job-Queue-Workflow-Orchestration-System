package com.djqueue.orchestrator.saga;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompensationHandler {

    public void compensate(List<SagaStep> completedSteps, String jobId) {
        log.info("Starting saga compensation rollback for jobId: {}", jobId);
        List<String> failedStepNames = new ArrayList<>();

        for (int i = completedSteps.size() - 1; i >= 0; i--) {
            SagaStep step = completedSteps.get(i);

            try {
                log.info("Compensating step: {} for jobId: {}", step.getName(), jobId);
                step.compensate(jobId);
            } catch (Exception e) {
                log.error("Failed compensation for step: {} on jobId: {}", step.getName(), jobId, e);
                failedStepNames.add(step.getName());
            }
        }

        if (!failedStepNames.isEmpty()) {
            throw new IllegalStateException("Saga compensation failed for steps: " + failedStepNames + " on jobId: " + jobId);
        }

        log.info("Successfully completed saga compensation rollback for jobId: {}", jobId);
    }
}