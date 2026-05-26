package com.djqueue.apigateway.routes;

import org.springframework.cloud.gateway.route.*;
import org.springframework.context.annotation.*;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {

        return builder.routes()

                // Producer Service
                .route("job-producer", r -> r
                        .path("/jobs/**")
                        .uri("http://job-producer:8081"))

                // Worker Admin
                .route("worker-service", r -> r
                        .path("/worker/**")
                        .uri("http://worker-service:8082"))

                // Retry Service
                .route("retry-service", r -> r
                        .path("/retry/**")
                        .uri("http://retry-orchestrator:8083"))

                // DLQ
                .route("dlq-service", r -> r
                        .path("/dlq/**")
                        .uri("http://dlq-processor:8084"))

                .build();
    }
}