package com.djqueue.apigateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter
        implements GlobalFilter, Ordered {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingFilter.class);

    private static final String START_TIME =
            "request-start-time";

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain
    ) {

        /*
         * Prevent duplicate logging
         * during reactive re-processing.
         */
        if (Boolean.TRUE.equals(
                exchange.getAttribute("request-logged"))
        ) {
            return chain.filter(exchange);
        }

        exchange.getAttributes()
                .put("request-logged", true);

        long start = System.currentTimeMillis();

        exchange.getAttributes()
                .put(START_TIME, start);

        ServerHttpRequest request =
                exchange.getRequest();

        String path =
                request.getURI().getPath();

        String method =
                request.getMethod() != null
                        ? request.getMethod().name()
                        : "UNKNOWN";

        String correlationId =
                request.getHeaders()
                        .getFirst("X-Correlation-ID");

        String userId =
                exchange.getAttribute("authenticatedUser");

        log.info(
                "Incoming request method={} path={} correlationId={} userId={}",
                method,
                path,
                correlationId,
                userId
        );

        return chain.filter(exchange)
                .doFinally(signalType -> {

                    long latency =
                            System.currentTimeMillis() - start;

                    int status =
                            exchange.getResponse()
                                    .getStatusCode() != null
                                    ? exchange.getResponse()
                                    .getStatusCode()
                                    .value()
                                    : 0;

                    log.info(
                            "Completed request method={} path={} status={} latencyMs={} correlationId={}",
                            method,
                            path,
                            status,
                            latency,
                            correlationId
                    );
                });
    }

    /**
     * Runs after authentication filter.
     */
    @Override
    public int getOrder() {
        return -50;
    }
}