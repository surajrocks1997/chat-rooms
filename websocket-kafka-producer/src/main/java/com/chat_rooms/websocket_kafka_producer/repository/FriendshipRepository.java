package com.chat_rooms.websocket_kafka_producer.repository;

import com.chat_rooms.websocket_kafka_producer.entity.FriendshipId;
import com.chat_rooms.websocket_kafka_producer.entity.Friendships;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendships, FriendshipId> {

    @Query("Select f from Friendships f where f.id.userA = :userId OR f.id.userB = :userId")
    List<Friendships> findAllByUser(Long userId);


}
