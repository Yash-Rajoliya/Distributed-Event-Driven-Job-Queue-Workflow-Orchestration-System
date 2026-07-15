package com.djqueue.producer.infrastructure.db;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobEntity {

    @Id
    private String id;

    private String payload;

    private String status;

    private long createdAt;
}