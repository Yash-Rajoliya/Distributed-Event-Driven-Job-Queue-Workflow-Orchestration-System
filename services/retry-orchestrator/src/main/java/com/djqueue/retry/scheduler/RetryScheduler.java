package com.djqueue.retry.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RetryScheduler {

    @Scheduled(fixedRate = 60000)
    public void monitorRetries() {
        System.out.println("Monitoring retry queue health...");
    }
}