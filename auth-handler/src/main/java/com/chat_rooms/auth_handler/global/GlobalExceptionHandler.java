package com.chat_rooms.auth_handler.global;

import com.chat_rooms.auth_handler.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    // for client specific errors
//    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
//    public ResponseEntity<ErrorResponse> handleBadRequestException(HttpClientErrorException.BadRequest ex, WebRequest request) {
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .statusCode(HttpStatus.BAD_REQUEST.value())
//                .errorMessage(ex.getMessage())
//                .timeStamp(LocalDateTime.now().toString())
//                .correlationId(Objects.requireNonNullElse(request.getHeader("x-correlation-id"), "some-correlation-id"))
//                .build();
//
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NoHandlerFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .errorMessage(ex.getMessage())
                .timeStamp(LocalDateTime.now().toString())
                .correlationId(Objects.requireNonNullElse(request.getHeader("x-correlation-id"), "some-correlation-id"))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage(ex.getMessage())
                .correlationId(Objects.requireNonNullElse(request.getHeader("x-correlation-id"), "some-correlation-id"))
                .timeStamp(LocalDateTime.now().toString())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
