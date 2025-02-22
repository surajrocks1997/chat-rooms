package com.chat_rooms.websocket_kafka_producer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomMessage {
    private MessageType messageType;
    private String username;
    private String userId;
    private ChatRoomName chatRoomName;
    private String message;
    private String timestamp;
    private Object additionalData;
}
