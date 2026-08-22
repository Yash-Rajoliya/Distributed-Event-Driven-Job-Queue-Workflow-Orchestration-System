package com.djqueue.worker.domain.handler;

import com.djqueue.worker.domain.model.Job;
import org.springframework.stereotype.Component;

@Component
public class PaymentJobHandler implements JobHandler {

    @Override
    public void handle(Job job) {
        System.out.println("Processing Payment Job: " + job.getId());
    }

    @Override
    public boolean supports(String payload) {
        return payload.contains("payment");
    }
}