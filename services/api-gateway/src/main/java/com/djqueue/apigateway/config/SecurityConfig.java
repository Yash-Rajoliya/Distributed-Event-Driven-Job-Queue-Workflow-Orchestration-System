package com.djqueue.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http
    ) {

        return http

                /*
                 * API gateway is stateless.
                 */
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

                .logout(ServerHttpSecurity.LogoutSpec::disable)

                /*
                 * Prevent security context persistence
                 * across reactive chains.
                 */
                .securityContextRepository(
                        org.springframework.security.web.server.context
                                .NoOpServerSecurityContextRepository
                                .getInstance()
                )

                .authorizeExchange(exchanges -> exchanges

                        /*
                         * Public endpoints
                         */
                        .pathMatchers(
                                "/health",
                                "/actuator/**",
                                "/auth/**"
                        ).permitAll()

                        /*
                         * Preflight CORS requests
                         */
                        .pathMatchers(HttpMethod.OPTIONS)
                        .permitAll()

                        /*
                         * All other routes require auth
                         */
                        .anyExchange()
                        .authenticated()
                )

                .build();
    }
}