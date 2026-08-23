package com.djqueue.orchestrator.saga;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompensationHandler {

    public void compensate(List<SagaStep> completedSteps, String jobId) {

        for (int i = completedSteps.size() - 1; i >= 0; i--) {
            SagaStep step = completedSteps.get(i);

            try {
                step.compensate(jobId);
            } catch (Exception e) {
                // log and continue
                System.out.println("Compensation failed for " + step.getName());
            }
        }
    }
}