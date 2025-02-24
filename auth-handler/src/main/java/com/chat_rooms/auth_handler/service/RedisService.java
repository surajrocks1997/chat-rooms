package com.chat_rooms.auth_handler.service;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final HashOperations<String, Object, Object> hashOps;
    private final ValueOperations<String, String> valueOps;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOps = this.redisTemplate.opsForHash();
        this.valueOps = this.redisTemplate.opsForValue();

    }

    public void setWithExpiry(String key, String value, int ttlInSeconds) {
        valueOps.set(key, value, Duration.ofSeconds(ttlInSeconds));
    }

    public void putWithExpiry(String key, String hashKey, String value, int ttlInSeconds) {
        hashOps.put(key, hashKey, value);
        redisTemplate.expire(key, Duration.ofSeconds(ttlInSeconds));
    }

    public void set(String key, String value) {
        valueOps.set(key, value);
    }

    public void put(String key, String hashKey, String value) {
        hashOps.put(key, hashKey, value);
    }

    public String getValueOps(String key) {
        return valueOps.get(key);
    }

    public String getHashOps(String key, String hashKey) {
        return (String) hashOps.get(key, hashKey);
    }

    public void deleteValueOps(String key) {
        redisTemplate.delete(key);
    }

    public void deleteHashOps(String key, String hashKey) {
        hashOps.delete(key, hashKey);
    }
}