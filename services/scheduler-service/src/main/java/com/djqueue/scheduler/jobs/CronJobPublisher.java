package com.djqueue.scheduler.jobs;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CronJobPublisher {

    @Scheduled(fixedRate = 60000)
    public void publish() {
        System.out.println("Publishing scheduled jobs...");
    }
}