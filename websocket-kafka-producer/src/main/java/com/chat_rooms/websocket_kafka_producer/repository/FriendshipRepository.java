package com.chat_rooms.websocket_kafka_producer.repository;

import com.chat_rooms.websocket_kafka_producer.entity.FriendshipId;
import com.chat_rooms.websocket_kafka_producer.entity.Friendships;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendships, FriendshipId> {
}
