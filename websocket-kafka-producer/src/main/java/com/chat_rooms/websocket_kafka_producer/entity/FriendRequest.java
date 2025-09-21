package com.chat_rooms.websocket_kafka_producer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "friend_request", uniqueConstraints = {@UniqueConstraint(name = "SenderId_ReceiverId_Unique", columnNames = {"senderId", "receiverId"})})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "senderId", nullable = false)
    private UserInfo sender;

    @ManyToOne
    @JoinColumn(name = "receiverId", nullable = false)
    private UserInfo receiver;

    @Enumerated(EnumType.STRING)
    private FriendRequestStatus status;

    @Column(nullable = false)
    private boolean isSeen = false;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

}
