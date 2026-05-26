package com.djqueue.apigateway.filters;

import org.slf4j.*;
import org.springframework.cloud.gateway.filter.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(org.springframework.web.server.ServerWebExchange exchange,
                            GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        log.info("Incoming request: {}", path);

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() ->
                        log.info("Response completed: {}", path)
                ));
    }
}