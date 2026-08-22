package com.djqueue.worker.application.service;

import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    public boolean allow() {
        return true; // Extend with token bucket later
    }
}