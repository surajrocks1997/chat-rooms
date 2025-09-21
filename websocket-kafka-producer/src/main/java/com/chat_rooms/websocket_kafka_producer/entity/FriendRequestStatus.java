package com.chat_rooms.websocket_kafka_producer.entity;

import lombok.Getter;

@Getter
public enum FriendRequestStatus {
    PENDING(0),
    ACCEPTED(1),
    REJECTED(2);

    private final int value;

    FriendRequestStatus(int value) {
        this.value = value;
    }
}
