package com.chat_rooms.websocket_kafka_producer.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ErrorResponse {
    private String correlationId;
    private String errorMessage;
    private int statusCode;
    private String timeStamp;
}
