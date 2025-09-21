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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<Long, Map<String, Object>> getSocialSummary(Long userId) {
        UserInfo user = userInfoRepository.getReferenceById(userId);
        Map<Long, Map<String, Object>> socialSummary = new HashMap<>();
        List<FriendRequest> pending = friendRequestRepository.findBySender(user);
        pending.forEach(request -> {
            Map<String, Object> valueMap = socialSummary.computeIfAbsent(request.getReceiver().getId(), k -> new HashMap<>());
            valueMap.put("id", request.getId());
            switch (request.getStatus().name()) {
                case "PENDING" -> valueMap.put("status", "FRIEND_REQUEST_SENT");
                case "ACCEPTED" -> valueMap.put("status", "FRIENDS");
                case "REJECTED" -> valueMap.put("status", "FRIEND_REQUEST_REJECTED");
                default -> valueMap.put("status", "UNKNOWN");
            }
            valueMap.put("isSeen", request.isSeen());
        });
        List<FriendRequest> received = friendRequestRepository.findByReceiver(user);
        received.forEach(request -> {
            Map<String, Object> valueMap = socialSummary.computeIfAbsent(request.getSender().getId(), k -> new HashMap<>());
            valueMap.put("id", request.getId());
            switch (request.getStatus().name()){
                case "PENDING" -> valueMap.put("status", "FRIEND_REQUEST_RECEIVED");
                case "ACCEPTED" -> valueMap.put("status", "FRIENDS");
                case "REJECTED" -> valueMap.put("status", "FRIEND_REQUEST_REJECTED");
                default -> valueMap.put("status", "UNKNOWN");
            }
            valueMap.put("isSeen", request.isSeen());
        });
        return socialSummary;
    }

    @Transactional
    public void acceptFriendRequest(Long requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException("Friend request id not found: " + requestId, HttpStatus.NOT_FOUND));

        if (friendRequest.getStatus() != FriendRequestStatus.PENDING)
            throw new CustomException("Friend Request Already Processed", HttpStatus.BAD_REQUEST);

        friendRequest.setStatus(FriendRequestStatus.ACCEPTED);
        friendRequestRepository.save(friendRequest);

        Long receiverId = friendRequest.getReceiver().getId();
        Long senderId = friendRequest.getReceiver().getId();
        FriendshipId friendshipId = receiverId.compareTo(senderId) < 0 ?
                new FriendshipId(receiverId, senderId) : new FriendshipId(senderId, receiverId);

        if (friendshipRepository.existsById(friendshipId))
            throw new CustomException("Friendship Already Exists", HttpStatus.BAD_REQUEST);
        friendshipRepository.save(Friendships.builder().id(friendshipId).build());
    }

}
