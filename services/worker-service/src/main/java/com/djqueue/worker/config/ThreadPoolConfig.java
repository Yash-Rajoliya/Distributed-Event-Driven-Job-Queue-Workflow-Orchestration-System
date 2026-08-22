package com.djqueue.worker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {

    @Bean(name = "workerExecutor")
    public Executor workerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Core pool size set to match available CPU cores / workload requirements
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(100);
        // Reduced queue capacity to prevent memory bloat and apply backpressure earlier
        executor.setQueueCapacity(250);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("worker-exec-");
        // AbortPolicy or custom handler prevents blocking Kafka consumer threads directly
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}