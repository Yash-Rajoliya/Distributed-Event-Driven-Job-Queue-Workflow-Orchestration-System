package com.djqueue.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.*;
import org.springframework.context.annotation.*;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange ->
                Mono.justOrEmpty(exchange.getRequest()
                        .getHeaders()
                        .getFirst("X-USER-ID"))
                        .defaultIfEmpty("anonymous");
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(10, 20); // 10 req/sec, burst 20
    }
}