package com.djqueue.apigateway.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class GatewayController {

    @GetMapping("/health")
    public String health() {
        return "API Gateway Running";
    }
}