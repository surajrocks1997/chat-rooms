package com.chat_rooms.auth_handler.global;

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
