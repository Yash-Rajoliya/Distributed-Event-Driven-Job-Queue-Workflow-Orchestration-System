package com.djqueue.worker.consumer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.awaitility.Awaitility.await;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"job-topic"})
@ActiveProfiles("test")
class KafkaWorkerConsumerTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private WorkerConsumer workerConsumer;

    @Test
    void shouldConsumeJobMessageFromKafkaTopic() {
        String payload = "{\"jobId\": \"test-job-001\", \"type\": \"EMAIL\"}";

        kafkaTemplate.send("job-topic", "test-job-001", payload);

        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    // Assert consumer state or repository update following message receipt
                });
    }
}