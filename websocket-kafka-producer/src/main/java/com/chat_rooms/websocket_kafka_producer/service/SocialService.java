package com.chat_rooms.websocket_kafka_producer.service;

import com.chat_rooms.websocket_kafka_producer.entity.*;
import com.chat_rooms.websocket_kafka_producer.global.CustomException;
import com.chat_rooms.websocket_kafka_producer.repository.FriendRequestRepository;
import com.chat_rooms.websocket_kafka_producer.repository.FriendshipRepository;
import com.chat_rooms.websocket_kafka_producer.repository.UserInfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserInfoRepository userInfoRepository;
    private final FriendshipRepository friendshipRepository;

    public void sendFriendRequest(Long senderId, Long receiverId) {
        friendRequestRepository.save(FriendRequest
                .builder()
                .sender(userInfoRepository.getReferenceById(senderId))
                .receiver(userInfoRepository.getReferenceById(receiverId))
                .status(FriendRequestStatus.PENDING)
                .build()
        );
    }

    public Map<String, Set<Long>> getFriendStatus(Long userId) {
        UserInfo user = userInfoRepository.getReferenceById(userId);
        Map<String, Set<Long>> friendsStatus = new HashMap<>();

        // Friend Request Received
        List<FriendRequest> pending = friendRequestRepository.findByReceiver(user);
        Set<Long> frSenderIds = new HashSet<>();
        pending.forEach(friendRequest -> frSenderIds.add(friendRequest.getSender().getId()));
        friendsStatus.put("PendingFRs", frSenderIds);

        // Friend Request Sent
        List<FriendRequest> sent = friendRequestRepository.findBySender(user);
        Set<Long> frReceiverIds = new HashSet<>();
        sent.forEach(friendRequest -> frReceiverIds.add(friendRequest.getReceiver().getId()));
        friendsStatus.put("SentFRs", frReceiverIds);

        // Active Friends
        Set<Long> friends = new HashSet<>();
        List<Friendships> friendships = friendshipRepository.findAllByUser(userId);
        friendships.forEach(friendship ->
                friends.add(friendship.getId().getUserA().equals(userId) ? friendship.getId().getUserB() : friendship.getId().getUserA()));
        friendsStatus.put("FRIENDS", friends);

        return friendsStatus;
    }

    @Transactional
    public void acceptFriendRequest(Long receiverId, Long senderId) {
        FriendRequest friendRequest = friendRequestRepository
                .findBySenderIdAndReceiverId(senderId, receiverId)
                .orElseThrow(() -> new CustomException("No Such Friend Request Exists!", HttpStatus.NOT_FOUND));


        if (friendRequest.getStatus() != FriendRequestStatus.PENDING)
            throw new CustomException("Friend Request Already Processed", HttpStatus.BAD_REQUEST);

        FriendshipId friendshipId = receiverId.compareTo(senderId) < 0 ?
                new FriendshipId(receiverId, senderId) : new FriendshipId(senderId, receiverId);

        if (friendshipRepository.existsById(friendshipId))
            throw new CustomException("Friendship Already Exists", HttpStatus.BAD_REQUEST);
        friendshipRepository.save(Friendships.builder().id(friendshipId).build());

        friendRequestRepository.delete(friendRequest);
    }

}
