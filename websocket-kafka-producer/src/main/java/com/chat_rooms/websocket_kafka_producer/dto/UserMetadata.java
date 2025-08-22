package com.chat_rooms.websocket_kafka_producer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserMetadata implements Serializable {
    private Long userId;
    private String email;
    private String connectedToServer;
}
