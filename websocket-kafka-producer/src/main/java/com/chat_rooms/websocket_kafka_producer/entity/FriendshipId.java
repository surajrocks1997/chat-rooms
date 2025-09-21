package com.chat_rooms.websocket_kafka_producer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FriendshipId implements Serializable {

    @Column(name = "userA")
    private Long userA;

    @Column(name = "userB")
    private Long userB;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FriendshipId)) return false;
        FriendshipId that = (FriendshipId) obj;
        return Objects.equals(userA, that.userA) && Objects.equals(userB, that.userB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userA, userB);
    }
}
