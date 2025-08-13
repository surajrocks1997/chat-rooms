package com.chat_rooms.websocket_kafka_producer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final HashOperations<String, Object, Object> hashOps;
    private final ValueOperations<String, String> valueOps;
    private final ListOperations<String, String> listOps;
    private final SetOperations<String, String> setOps;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOps = this.redisTemplate.opsForHash();
        this.valueOps = this.redisTemplate.opsForValue();
        this.listOps = this.redisTemplate.opsForList();
        this.setOps = this.redisTemplate.opsForSet();

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

    public String getHashOps(String key, String hashKey) {
        return (String) hashOps.get(key, hashKey);
    }

    public String getValueOps(String key) {
        return valueOps.get(key);
    }

    public void deleteValueOps(String key) {
        redisTemplate.delete(key);
    }

    public void deleteHashOps(String key, String hashKey) {
        hashOps.delete(key, hashKey);
    }

    public void addToList(String key, String value) {
        listOps.rightPush(key, value);
    }

    public void removeFromList(String key, String value) {
        listOps.remove(key, 1, value);
    }

    public void addToSet(String key, String value) {
        setOps.add(key, value);
    }

    public void removeFromSet(String key, String value) {
        setOps.remove(key, value);
    }

    public boolean isSetEmpty(String key) {
        log.info("RedisService: Size Of Key: {} = {}", key, setOps.size(key));
        return setOps.size(key) == 0;
    }
}