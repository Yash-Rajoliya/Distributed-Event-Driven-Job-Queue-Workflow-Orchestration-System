package com.djqueue.common.config;

import com.djqueue.common.dto.v1.JobEventV1;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    private static final String BOOTSTRAP_SERVERS = "kafka:9092";
    private static final String CONSUMER_GROUP = "djqueue-group";

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {

        Map<String, Object> props = new HashMap<>();

        /*
         * Core Consumer Configuration
         */
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP);

        /*
         * Disable auto commit for exactly-once-effect semantics
         */
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        /*
         * Offset recovery strategy
         */
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        /*
         * Consumer stability tuning
         */
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 5000);

        /*
         * Safe deserialization wrapper
         */
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                ErrorHandlingDeserializer.class
        );

        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                ErrorHandlingDeserializer.class
        );

        /*
         * Delegate deserializers
         */
        props.put(
                ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS,
                StringDeserializer.class
        );

        props.put(
                ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
                JsonDeserializer.class
        );

        /*
         * Prevent arbitrary class deserialization attacks
         */
        props.put(
                JsonDeserializer.TRUSTED_PACKAGES,
                "com.djqueue.common.dto"
        );

        /*
         * Default DTO fallback
         */
        props.put(
                JsonDeserializer.VALUE_DEFAULT_TYPE,
                JobEventV1.class.getName()
        );

        /*
         * Allow non-type-header producers
         */
        props.put(
                JsonDeserializer.USE_TYPE_INFO_HEADERS,
                false
        );

        /*
         * Prevent startup crashes on malformed historical offsets
         */
        props.put(
                ConsumerConfig.DEFAULT_API_TIMEOUT_MS_CONFIG,
                30000
        );

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        /*
         * Parallel partition consumption
         */
        factory.setConcurrency(6);

        /*
         * Manual acknowledgement after successful processing
         */
        factory.getContainerProperties()
                .setAckMode(ContainerProperties.AckMode.MANUAL);

        /*
         * Graceful shutdown for in-flight jobs
         */
        factory.getContainerProperties()
                .setShutdownTimeout(10000);

        /*
         * Detect idle consumers for observability
         */
        factory.getContainerProperties()
                .setIdleEventInterval(Duration.ofMinutes(1).toMillis());

        /*
         * Prevent listener startup failure due to poison payloads
         */
        factory.setMissingTopicsFatal(false);

        return factory;
    }
}