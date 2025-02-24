package com.chat_rooms.auth_handler.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoggingUtil {

    public void logException(Exception ex, String correlationId) {
        log.error("Exception occurred: {}", ex.getMessage(), ex);
    }

    public void logStructuredMessage(Object message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMessage = objectMapper.writeValueAsString(message);
        log.error("Error Log: {}", jsonMessage);
    }
}
