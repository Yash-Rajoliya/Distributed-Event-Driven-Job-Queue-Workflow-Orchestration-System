package com.djqueue.worker.domain.handler;

import com.djqueue.worker.domain.model.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultJobHandler implements JobHandler {

    @Override
    public void handle(Job job) {
        log.warn("Executing fallback handler for unsupported payload job: {}", job.getId());
    }

    @Override
    public boolean supports(String payload) {
        // Acts as fallback when no specific handler supports the payload
        return true;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE; // Ensures specific handlers are evaluated first
    }
}