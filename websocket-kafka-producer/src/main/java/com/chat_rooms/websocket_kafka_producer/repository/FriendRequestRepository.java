package com.chat_rooms.websocket_kafka_producer.repository;

import com.chat_rooms.websocket_kafka_producer.entity.FriendRequest;
import com.chat_rooms.websocket_kafka_producer.entity.FriendRequestStatus;
import com.chat_rooms.websocket_kafka_producer.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    // All requests sent by a user
    List<FriendRequest> findBySender(UserInfo sender);

    // All requests received by a user
    List<FriendRequest> findByReceiver(UserInfo receiver);

    // Optional: filter by status
    List<FriendRequest> findBySenderAndStatus(UserInfo sender, FriendRequestStatus status);
    List<FriendRequest> findByReceiverAndStatus(UserInfo receiver, FriendRequestStatus status);

}
