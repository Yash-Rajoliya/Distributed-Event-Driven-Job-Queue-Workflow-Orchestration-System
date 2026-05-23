package com.djqueue.common.interceptors;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.*;
import java.util.UUID;

@Component
public class CorrelationIdInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        MDC.put("correlationId", UUID.randomUUID().toString());
        return true;
    }
}