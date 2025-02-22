package com.chat_rooms.auth_handler.service;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final HashOperations<String, Object, Object> ops;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.ops = this.redisTemplate.opsForHash();

    }

    public void putWithExpiry(String key, String valueVarName, String value, int ttlInSeconds) {
        ops.put(key, valueVarName, value);
        redisTemplate.expire(key, Duration.ofSeconds(ttlInSeconds));
    }

    public void put(String key, String valueVarName, String value) {
        ops.put(key, valueVarName, value);
    }

    public String get(String key, String valueVarName) {
        return (String) ops.get(key, valueVarName);
    }
}
