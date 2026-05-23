package com.djqueue.common.tracing;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class TracingConfig {

    private static final String TRACER_NAME = "distributed-job-queue";

    /**
     * Shared tracer instance across all services.
     */
    @Bean
    public Tracer tracer() {
        return GlobalOpenTelemetry.getTracer(TRACER_NAME);
    }

    /**
     * Propagates OpenTelemetry trace context
     * across async thread boundaries.
     */
    @Bean
    public TaskDecorator tracingTaskDecorator() {
        return runnable -> {

            Context parentContext = Context.current();

            return () -> {
                try (var scope = parentContext.makeCurrent()) {
                    runnable.run();
                }
            };
        };
    }

    /**
     * Context-aware executor for async workloads.
     *
     * Used by:
     * - worker execution
     * - retry scheduling
     * - saga orchestration
     * - async event publication
     */
    @Bean(name = "tracingExecutor")
    public Executor tracingExecutor(
            TaskDecorator tracingTaskDecorator
    ) {

        ThreadPoolTaskExecutor executor =
                new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(64);
        executor.setQueueCapacity(500);

        executor.setThreadNamePrefix("djqueue-trace-");

        executor.setTaskDecorator(tracingTaskDecorator);

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);

        executor.initialize();

        return executor;
    }
}