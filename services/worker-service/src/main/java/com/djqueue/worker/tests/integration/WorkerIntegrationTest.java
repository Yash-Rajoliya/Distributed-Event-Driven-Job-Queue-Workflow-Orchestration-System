package com.djqueue.worker;

import com.djqueue.worker.processor.JobWorkerProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class WorkerIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private JobWorkerProcessor jobWorkerProcessor;

    @BeforeEach
    void setUp() {
        // Reset shared mock state or clear test database tables if present
    }

    @Test
    void shouldLoadApplicationContextAndWireBeans() {
        assertNotNull(applicationContext, "Worker application context should load successfully");
        assertNotNull(jobWorkerProcessor, "JobWorkerProcessor bean should be loaded into context");
    }
}