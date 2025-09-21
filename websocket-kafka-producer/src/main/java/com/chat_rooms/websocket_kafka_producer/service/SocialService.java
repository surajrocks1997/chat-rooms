package com.chat_rooms.websocket_kafka_producer.service;

import com.chat_rooms.websocket_kafka_producer.entity.FriendRequest;
import com.chat_rooms.websocket_kafka_producer.entity.FriendRequestStatus;
import com.chat_rooms.websocket_kafka_producer.entity.UserInfo;
import com.chat_rooms.websocket_kafka_producer.repository.FriendRequestRepository;
import com.chat_rooms.websocket_kafka_producer.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserInfoRepository userInfoRepository;

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
        List<FriendRequest> pending = friendRequestRepository.findBySenderAndStatus(user, FriendRequestStatus.PENDING);
        pending.forEach(request -> {
            Map<String, Object> valueMap = socialSummary.computeIfAbsent(request.getReceiver().getId(), k -> new HashMap<>());
            valueMap.put("status", "FRIEND_REQUEST_SENT");
            valueMap.put("isSeen", request.isSeen());
        });
        List<FriendRequest> received = friendRequestRepository.findByReceiverAndStatus(user, FriendRequestStatus.PENDING);
        received.forEach(request -> {
            Map<String, Object> valueMap = socialSummary.computeIfAbsent(request.getSender().getId(), k -> new HashMap<>());
            valueMap.put("status", "FRIEND_REQUEST_RECEIVED");
            valueMap.put("isSeen", request.isSeen());
        });
        return socialSummary;
    }

}
