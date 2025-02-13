package com.chat_rooms.websocket_kafka_producer.security;

import lombok.Getter;

import java.security.Principal;

public class UserRoleDetails implements Principal {
    private final String sub;
    @Getter
    private final Long id;

    public UserRoleDetails(String sub, Long id) {
        this.sub = sub;
        this.id = id;
    }

    @Override
    public String getName() {
        return this.sub;
    }

}
