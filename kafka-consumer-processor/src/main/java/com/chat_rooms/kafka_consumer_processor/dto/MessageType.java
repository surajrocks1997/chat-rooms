package com.chat_rooms.kafka_consumer_processor.dto;

public enum MessageType {
    USER_ONLINE,
    USER_OFFLINE,
    USER_TYPING,
    CHAT_MESSAGE,
    PRIVATE_MESSAGE,
    SEND_FRIEND_REQUEST,
}
