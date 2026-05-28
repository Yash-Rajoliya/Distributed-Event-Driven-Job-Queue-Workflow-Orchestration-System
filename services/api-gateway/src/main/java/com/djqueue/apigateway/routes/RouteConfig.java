package com.djqueue.apigateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.time.Duration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator routes(
            RouteLocatorBuilder builder
    ) {

        return builder.routes()

                /*
                 * Job Producer Service
                 */
                .route("job-producer", r -> r
                        .path("/jobs/**")
                        .and()
                        .method(
                                HttpMethod.POST,
                                HttpMethod.GET
                        )
                        .filters(f -> f

                                /*
                                 * Prevent duplicate retries
                                 * on non-idempotent writes.
                                 */
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(HttpMethod.GET)
                                )

                                .requestRateLimiter(config -> {
                                    config.setRateLimiter(
                                            new org.springframework.cloud.gateway
                                                    .filter.ratelimit.RedisRateLimiter(
                                                    100,
                                                    300
                                            )
                                    );
                                })

                                .addResponseHeader(
                                        "X-Gateway",
                                        "djqueue-api-gateway"
                                )
                        )
                        .uri("http://job-producer:8081")
                )

                /*
                 * Worker Admin APIs
                 */
                .route("worker-service", r -> r
                        .path("/worker/**")
                        .filters(f -> f
                                .circuitBreaker(cb -> cb
                                        .setName("worker-circuit-breaker")
                                )
                        )
                        .uri("http://worker-service:8082")
                )

                /*
                 * Retry Orchestrator
                 */
                .route("retry-service", r -> r
                        .path("/retry/**")
                        .filters(f -> f
                                .circuitBreaker(cb -> cb
                                        .setName("retry-circuit-breaker")
                                )
                        )
                        .uri("http://retry-orchestrator:8083")
                )

                /*
                 * DLQ Processor
                 */
                .route("dlq-service", r -> r
                        .path("/dlq/**")
                        .filters(f -> f
                                .circuitBreaker(cb -> cb
                                        .setName("dlq-circuit-breaker")
                                )
                        )
                        .uri("http://dlq-processor:8084")
                )

                .build();
    }
}