package com.chat_rooms.websocket_kafka_producer.entity;

import lombok.Getter;

@Getter
public enum AuthProvider {
    LOCAL("LOCAL"),
    GOOGLE("GOOGLE");

    private final String value;

    AuthProvider(String value) {
        this.value = value;
    }

}
