package com.djqueue.worker.domain.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    private String id;
    private String payload;
    private int retryCount;
}