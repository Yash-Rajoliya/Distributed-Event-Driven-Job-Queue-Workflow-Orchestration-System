package com.djqueue.worker.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IdempotencyRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean isProcessed(String jobId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("job:" + jobId));
    }

    public void markProcessed(String jobId) {
        redisTemplate.opsForValue().set("job:" + jobId, "done");
    }
}