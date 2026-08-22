package com.djqueue.worker.domain.handler;

import com.djqueue.worker.domain.model.Job;

public interface JobHandler {
    void handle(Job job);
    boolean supports(String payload);
}