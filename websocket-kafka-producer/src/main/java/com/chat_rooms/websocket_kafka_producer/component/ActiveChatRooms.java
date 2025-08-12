package com.chat_rooms.websocket_kafka_producer.component;

import com.chat_rooms.websocket_kafka_producer.service.KafkaConsumerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@RequiredArgsConstructor
public class ActiveChatRooms {

    private final Map<String, Set<String>> chatRooms = new ConcurrentHashMap<>();
    private final KafkaConsumerService kafkaConsumerService;

    public void addSubscriber(String chatRoomName, String sessionId, String username, Long userId) {
        chatRooms.compute(chatRoomName, (name, sessions) -> {
            if (sessions == null) {
                sessions = new CopyOnWriteArraySet<>();
                kafkaConsumerService.startListener(chatRoomName);
            }
            sessions.add(sessionId);
            return sessions;
        });
    }

    public void removeSubscriber(String chatRoomName, String sessionId, String username, Long userId) {
        chatRooms.computeIfPresent(chatRoomName, (name, sessions) -> {
            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                kafkaConsumerService.stopListener(chatRoomName);
                return null;
            }
            return sessions;
        });
    }

    public Set<String> getActiveChatRooms() {
        return chatRooms.keySet();
    }

    public Set<String> getSubscribers(String chatRoomName) {
        return chatRooms.get(chatRoomName);
    }
}
