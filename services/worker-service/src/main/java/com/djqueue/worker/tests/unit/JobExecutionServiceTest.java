package com.djqueue.worker.tests.unit;

import com.djqueue.worker.application.service.JobExecutionService;
import com.djqueue.worker.application.service.IdempotencyService;
import com.djqueue.worker.application.service.RetryService;
import com.djqueue.worker.domain.model.Job;
import com.djqueue.worker.domain.handler.JobHandler;
import com.djqueue.worker.infrastructure.db.JobExecutionEntity;
import com.djqueue.worker.metrics.WorkerMetrics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JobExecutionServiceTest {

    @Mock
    private IdempotencyService idempotencyService;

    @Mock
    private RetryService retryService;

    @Mock
    private JobHandler jobHandler;

    @Mock
    private WorkerMetrics metrics;

    @InjectMocks
    private JobExecutionService jobExecutionService;

    private Job job;

    @BeforeEach
    void setup() {
        job = new Job();
        job.setJobId("job-123");
        job.setType("EMAIL");
        job.setPayload("test");
    }

    // ✅ SUCCESS CASE
    @Test
    void shouldProcessJobSuccessfully() {

        when(idempotencyService.isDuplicate("job-123")).thenReturn(false);
        doNothing().when(jobHandler).handle(job);

        jobExecutionService.execute(job);

        verify(jobHandler, times(1)).handle(job);
        verify(idempotencyService, times(1)).markProcessed("job-123");
        verify(metrics, times(1)).success();

        verify(retryService, never()).retry(any());
    }

    // ✅ DUPLICATE CASE (IDEMPOTENCY)
    @Test
    void shouldSkipDuplicateJob() {

        when(idempotencyService.isDuplicate("job-123")).thenReturn(true);

        jobExecutionService.execute(job);

        verify(jobHandler, never()).handle(any());
        verify(metrics, never()).success();
        verify(retryService, never()).retry(any());
    }

    // ✅ FAILURE → RETRY CASE
    @Test
    void shouldRetryOnFailure() {

        when(idempotencyService.isDuplicate("job-123")).thenReturn(false);

        doThrow(new RuntimeException("failure"))
                .when(jobHandler).handle(job);

        jobExecutionService.execute(job);

        verify(retryService, times(1)).retry(job);
        verify(metrics, times(1)).failure();
    }

    // ✅ EDGE CASE: NULL JOB
    @Test
    void shouldThrowExceptionForNullJob() {

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            jobExecutionService.execute(null);
        });

        assertEquals("Job cannot be null", ex.getMessage());
    }
}