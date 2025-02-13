package com.chat_rooms.auth_handler.global;

import com.chat_rooms.auth_handler.dto.ErrorResponse;
import com.chat_rooms.auth_handler.utils.LoggingUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final String CORRELATION_ID_LOG_VAR_NAME = "correlationId";
    private final LoggingUtil loggingUtil;

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

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex, WebRequest request) {
        String correlationId = MDC.get(CORRELATION_ID_LOG_VAR_NAME);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(ex.getStatus().value())
                .errorMessage(ex.getMessage())
                .correlationId(correlationId)
                .timeStamp(LocalDateTime.now().toString())
                .build();

        return new ResponseEntity<>(errorResponse, ex.getStatus());

    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(DataAccessException ex, WebRequest request) {
        String correlationId = MDC.get(CORRELATION_ID_LOG_VAR_NAME);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage("Database Error : " + ex.getMessage())
                .correlationId(correlationId)
                .timeStamp(LocalDateTime.now().toString())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NoHandlerFoundException ex, WebRequest request) {
        String correlationId = MDC.get(CORRELATION_ID_LOG_VAR_NAME);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .errorMessage("Not Found Exception : " + ex.getMessage())
                .timeStamp(LocalDateTime.now().toString())
                .correlationId(correlationId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) throws JsonProcessingException {
        String correlationId = MDC.get(CORRELATION_ID_LOG_VAR_NAME);

        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(error -> ((FieldError) error).getField() + " : " + error.getDefaultMessage() + ". ")
                .collect(Collectors.joining());


        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage("Validation Exception : " + errorMessage)
                .correlationId(correlationId)
                .timeStamp(LocalDateTime.now().toString())
                .build();

        loggingUtil.logException(ex, correlationId);
        loggingUtil.logStructuredMessage(errorResponse);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) throws JsonProcessingException {
        String correlationId = MDC.get(CORRELATION_ID_LOG_VAR_NAME);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage("Generic Exception : " + ex.getMessage())
                .correlationId(correlationId)
                .timeStamp(LocalDateTime.now().toString())
                .build();

        loggingUtil.logException(ex, correlationId);
        loggingUtil.logStructuredMessage(errorResponse);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
