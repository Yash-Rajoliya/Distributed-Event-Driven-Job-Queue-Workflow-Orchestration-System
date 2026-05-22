package com.djqueue.common.locking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DistributedLockService {

    private final RedisTemplate<String, Object> redis;

    public boolean lock(String key) {
        return Boolean.TRUE.equals(redis.opsForValue()
                .setIfAbsent("lock:" + key, "locked"));
    }

    public void unlock(String key) {
        redis.delete("lock:" + key);
    }
}