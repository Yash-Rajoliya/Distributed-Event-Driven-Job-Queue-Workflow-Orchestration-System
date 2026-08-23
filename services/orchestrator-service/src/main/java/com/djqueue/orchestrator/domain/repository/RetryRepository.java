package com.djqueue.retry.domain.repository;

import com.djqueue.retry.domain.model.RetryJob;
import java.util.List;

public interface RetryRepository {

    void save(RetryJob job);

    List<RetryJob> findDueJobs();

    void delete(String jobId);
}