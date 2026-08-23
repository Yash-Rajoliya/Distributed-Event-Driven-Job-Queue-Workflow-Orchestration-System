package com.djqueue.orchestrator.saga;

import org.springframework.core.Ordered;

public interface SagaStep extends Ordered {

    String getName();

    void execute(String jobId);

    void compensate(String jobId);

    @Override
    default int getOrder() {
        return 0;
    }
}