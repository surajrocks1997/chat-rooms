package com.chat_rooms.websocket_kafka_producer.dto;

public record RedisSubscriberChangedEvent(String room, boolean hasSubscribed) {
}
