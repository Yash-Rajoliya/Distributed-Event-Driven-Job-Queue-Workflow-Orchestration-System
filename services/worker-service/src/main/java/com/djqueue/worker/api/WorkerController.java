package com.djqueue.worker.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/worker")
public class WorkerController {

    @GetMapping("/health")
    public String health() {
        return "Worker running";
    }
}