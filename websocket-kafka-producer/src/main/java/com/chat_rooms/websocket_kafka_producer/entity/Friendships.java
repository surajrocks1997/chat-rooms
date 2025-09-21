package com.chat_rooms.websocket_kafka_producer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "friendships")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Friendships {

    @EmbeddedId
    private FriendshipId id;

    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
