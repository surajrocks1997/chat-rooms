package com.chat_rooms.websocket_kafka_producer.dto;

public enum MessageType {
    USER_ONLINE,
    USER_OFFLINE,
    USER_TYPING,
    CHAT_MESSAGE,
    PRIVATE_MESSAGE,
    SEND_FRIEND_REQUEST,
}
