package com.chat_rooms.websocket_kafka_producer.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class JsonRedisService {

    private final RedisTemplate<String, Object> redisJsonTemplate;
    private final ValueOperations<String, Object> valueOps;

    public JsonRedisService(RedisTemplate<String, Object> redisJsonTemplate) {
        this.redisJsonTemplate = redisJsonTemplate;
        this.valueOps = redisJsonTemplate.opsForValue();
    }

    public void set(String key, Object value) {
        valueOps.set(key, value);
    }

    public Object get(String key) {
        return valueOps.get(key);
    }

    public void setWithExpiry(String key, Object value, int ttlInSeconds) {
        valueOps.set(key, value, Duration.ofSeconds(ttlInSeconds));
    }

    public void delete(String key) {
        redisJsonTemplate.delete(key);
    }
}
