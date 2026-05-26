package com.djqueue.apigateway.filters;

import com.djqueue.common.security.JwtUtil;
import org.springframework.cloud.gateway.filter.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String auth = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        if (auth == null || !auth.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        try {
            String token = auth.substring(7);
            String user = JwtUtil.validate(token);

            exchange.getRequest().mutate()
                    .header("X-USER-ID", user)
                    .build();

        } catch (Exception e) {
            return unauthorized(exchange);
        }

        return chain.filter(exchange);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(
                org.springframework.http.HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}