package com.chat_rooms.websocket_kafka_producer.dto;

import lombok.Getter;

@Getter
public enum MessageType {
    USER_ONLINE("USER_ONLINE"),
    USER_OFFLINE("USER_OFFLINE"),
    USER_TYPING("USER_TYPING"),
    CHAT_MESSAGE("CHAT_MESSAGE"),
    PRIVATE_MESSAGE("PRIVATE_MESSAGE"),
    SEND_FRIEND_REQUEST("SEND_FRIEND_REQUEST"),
    START_LISTENER("START_LISTENER"),
    STOP_LISTENER("STOP_LISTENER");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }
}
