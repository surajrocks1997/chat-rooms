package com.chat_rooms.websocket_kafka_producer.dto;

import lombok.Getter;

@Getter
public enum ChatRoomName {
    Sports("Sports"),
    Technology("Technology"),
    Science("Science"),
    Automobile("Automobile"),
    Gadgets("Gadgets"),
    News("News"),
    Random("Random");

    private final String value;

    ChatRoomName(String value) {
        this.value = value;
    }
}
