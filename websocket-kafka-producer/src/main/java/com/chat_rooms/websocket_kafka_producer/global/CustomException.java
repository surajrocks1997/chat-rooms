package com.chat_rooms.websocket_kafka_producer.global;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private HttpStatus status;

    public CustomException(String errorMessage, HttpStatus status) {
        super(errorMessage);
        this.status = status;
    }
}
