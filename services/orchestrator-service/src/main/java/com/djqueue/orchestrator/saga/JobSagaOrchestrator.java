package com.djqueue.orchestrator.saga;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class JobSagaOrchestrator {

    private final CompensationHandler compensationHandler;
    private final List<SagaStep> steps;

    public void startSaga(String jobId) {

        List<SagaStep> completedSteps = new ArrayList<>();

        try {

            for (SagaStep step : steps) {
                step.execute(jobId);
                completedSteps.add(step);
            }

            System.out.println("Saga completed successfully for job: " + jobId);

        } catch (Exception e) {

            System.out.println("Saga failed. Triggering compensation...");
            compensationHandler.compensate(completedSteps, jobId);
        }
    }
}