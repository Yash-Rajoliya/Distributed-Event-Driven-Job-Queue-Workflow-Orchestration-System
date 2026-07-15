package com.djqueue.producer.api;

import com.djqueue.common.dto.ApiResponse;
import com.djqueue.producer.application.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ApiResponse<String> createJob(@RequestBody String payload) {
        String jobId = jobService.createJob(payload);
        return new ApiResponse<>(true, jobId, "Job submitted successfully");
    }
}