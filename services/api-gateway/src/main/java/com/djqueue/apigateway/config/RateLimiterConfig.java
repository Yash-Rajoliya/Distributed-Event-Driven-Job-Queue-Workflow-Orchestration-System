package com.djqueue.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    private static final String USER_HEADER =
            "X-USER-ID";

    private static final String TENANT_HEADER =
            "X-TENANT-ID";

    /**
     * Tenant-aware rate limiting.
     *
     * Prevents:
     * - anonymous key collision
     * - cross-tenant throttling
     * - burst amplification
     */
    @Bean
    public KeyResolver userKeyResolver() {

        return exchange -> {

            String tenantId =
                    exchange.getRequest()
                            .getHeaders()
                            .getFirst(TENANT_HEADER);

            String userId =
                    exchange.getRequest()
                            .getHeaders()
                            .getFirst(USER_HEADER);

            /*
             * Authenticated tenant-aware identity.
             */
            if (tenantId != null && userId != null) {
                return Mono.just(
                        tenantId + ":" + userId
                );
            }

            /*
             * Authenticated fallback.
             */
            if (userId != null) {
                return Mono.just(userId);
            }

            /*
             * IP-based fallback for anonymous traffic.
             */
            String ip =
                    exchange.getRequest()
                            .getRemoteAddress() != null
                            ? exchange.getRequest()
                            .getRemoteAddress()
                            .getAddress()
                            .getHostAddress()
                            : "unknown";

            return Mono.just("anonymous:" + ip);
        };
    }

    /**
     * Adaptive burst handling.
     *
     * replenishRate:
     * steady-state requests/sec
     *
     * burstCapacity:
     * short-term traffic spikes
     */
    @Bean
    public RedisRateLimiter redisRateLimiter() {

        return new RedisRateLimiter(
                100,
                300,
                1
        );
    }
}