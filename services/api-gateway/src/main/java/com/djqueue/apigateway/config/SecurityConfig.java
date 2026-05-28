package com.djqueue.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http
    ) {

        return http

                /*
                 * Stateless gateway architecture.
                 */
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

                .logout(ServerHttpSecurity.LogoutSpec::disable)

                /*
                 * Avoid duplicate reactive security persistence.
                 */
                .securityContextRepository(
                        NoOpServerSecurityContextRepository.getInstance()
                )

                .authorizeExchange(exchange -> exchange

                        /*
                         * Public endpoints.
                         */
                        .pathMatchers(
                                "/health",
                                "/actuator/**",
                                "/auth/**"
                        ).permitAll()

                        /*
                         * Allow CORS preflight.
                         */
                        .pathMatchers(HttpMethod.OPTIONS)
                        .permitAll()

                        /*
                         * Everything else authenticated.
                         */
                        .anyExchange()
                        .authenticated()
                )

                .build();
    }
}