package com.djqueue.producer.domain.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    private String id;
    private String payload;
    private String status;
    private long createdAt;
}