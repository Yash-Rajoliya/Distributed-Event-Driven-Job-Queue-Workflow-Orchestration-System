package com.djqueue.producer.domain.repository;

import com.djqueue.producer.domain.model.Job;

public interface JobRepository {
    Job save(Job job);
}