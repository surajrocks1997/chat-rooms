package com.chat_rooms.websocket_kafka_producer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class JsonRedisService {

    private final RedisTemplate<String, String> redisJsonTemplate;
    private final ValueOperations<String, String> valueOps;
    private final ObjectMapper objectMapper;

    public JsonRedisService(RedisTemplate<String, String> redisJsonTemplate, ObjectMapper objectMapper) {
        this.redisJsonTemplate = redisJsonTemplate;
        this.valueOps = redisJsonTemplate.opsForValue();
        this.objectMapper = objectMapper;
    }

    public void set(String key, Object value) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(value);
        valueOps.set(key, json);
    }

    public <T> T get(String key, Class<T> classType) throws JsonProcessingException {
        String json = valueOps.get(key);
        return objectMapper.readValue(json, classType);
    }

    public void setWithExpiry(String key, Object value, int ttlInSeconds) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(value);
        valueOps.set(key, json, Duration.ofSeconds(ttlInSeconds));
    }

    public <T> List<T> getAll(List<String> keys, Class<T> classType) {
        List<String> jsonStrings = valueOps.multiGet(keys);
        assert jsonStrings != null;
        return jsonStrings.stream().map((json) -> {
            try {
                return objectMapper.readValue(json, classType);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    public void delete(String key) {
        redisJsonTemplate.delete(key);
    }

    public void publish(String channel, Object message) throws JsonProcessingException {
        redisJsonTemplate.convertAndSend(channel, objectMapper.writeValueAsString(message));
    }
}
