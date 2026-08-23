package com.djqueue.orchestrator.saga;

public interface SagaStep {

    String getName();

    void execute(String jobId);

    void compensate(String jobId);
}