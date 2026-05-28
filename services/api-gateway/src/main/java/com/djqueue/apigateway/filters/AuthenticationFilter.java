package com.djqueue.apigateway.filters;

import com.djqueue.common.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter
        implements GlobalFilter, Ordered {

    private static final Logger log =
            LoggerFactory.getLogger(AuthenticationFilter.class);

    private static final String USER_ID_HEADER = "X-USER-ID";

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain
    ) {

        /*
         * Prevent duplicate filter execution
         * during reactive chain re-processing.
         */
        if (Boolean.TRUE.equals(
                exchange.getAttribute("auth_filter_applied"))
        ) {
            return chain.filter(exchange);
        }

        exchange.getAttributes()
                .put("auth_filter_applied", true);

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            return unauthorized(exchange);
        }

        try {

            String token = authHeader.substring(7);

            String userId = JwtUtil.validate(token);

            /*
             * Proper immutable request mutation
             * for reactive gateway chains.
             */
            ServerHttpRequest mutatedRequest =
                    exchange.getRequest()
                            .mutate()
                            .header(USER_ID_HEADER, userId)
                            .build();

            ServerWebExchange mutatedExchange =
                    exchange.mutate()
                            .request(mutatedRequest)
                            .build();

            /*
             * Store auth metadata once
             * to avoid duplicate downstream logging.
             */
            mutatedExchange.getAttributes()
                    .put("authenticatedUser", userId);

            return chain.filter(mutatedExchange);

        } catch (Exception ex) {

            log.debug(
                    "JWT validation failed: {}",
                    ex.getMessage()
            );

            return unauthorized(exchange);
        }
    }

    private Mono<Void> unauthorized(
            ServerWebExchange exchange
    ) {

        exchange.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);

        return exchange.getResponse().setComplete();
    }

    /**
     * Execute before logging filters
     * to prevent duplicate request logging.
     */
    @Override
    public int getOrder() {
        return -100;
    }
}