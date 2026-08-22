package com.djqueue.worker.domain.handler;

import com.djqueue.worker.domain.model.Job;
import org.springframework.core.Ordered;

public interface JobHandler extends Ordered {
    void handle(Job job);
    boolean supports(String payload);

    @Override
    default int getOrder() {
        return 0; // Default priority for specific handlers
    }
}