package com.djqueue.worker.config;

import org.springframework.context.annotation.*;
import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {

    @Bean
    public ExecutorService workerExecutor() {
        return new ThreadPoolExecutor(
                10,
                50,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}