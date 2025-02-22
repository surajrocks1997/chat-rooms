package com.chat_rooms.websocket_kafka_producer.dto;

public interface UserInfoProjection {

    Long getId();

    String getFirstName();

    String getLastName();

    String getEmail();

    String getUsername();

    String getProfilePictureUrl();
}
