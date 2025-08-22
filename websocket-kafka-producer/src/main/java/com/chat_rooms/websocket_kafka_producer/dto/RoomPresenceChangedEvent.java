package com.chat_rooms.websocket_kafka_producer.dto;

public record RoomPresenceChangedEvent(String room, String sessionId, boolean hasJoined){}
