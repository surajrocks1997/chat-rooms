package com.chat_rooms.websocket_kafka_producer.service;

import com.chat_rooms.websocket_kafka_producer.dto.ChatRoomMessage;
import com.chat_rooms.websocket_kafka_producer.utils.RedisKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final JsonRedisService jsonRedisService;
    private final ConcurrentKafkaListenerContainerFactory<String, ChatRoomMessage> chatRoomEventListenerContainerFactory;
    private final Map<String, ConcurrentMessageListenerContainer<String, ChatRoomMessage>> listenerContainers = new ConcurrentHashMap<>();

    public void startListener(String chatRoomName) {
//        listenerContainers.computeIfAbsent(chatRoomName, name -> {
//            ConcurrentMessageListenerContainer<String, ChatRoomMessage> container = chatRoomEventListenerContainerFactory.createContainer("chat-room-topic-" + name);
//            ContainerProperties containerProps = container.getContainerProperties();
//            containerProps.setGroupId("chat-room-consumer");
//            containerProps.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
//
//            container.setupMessageListener((AcknowledgingMessageListener<String, ChatRoomMessage>) (record, ack) -> {
//                try {
//                    jsonRedisService.publish(RedisKeys.BASE + record.value().getChatRoomName().getValue(), record.value());
//                    ack.acknowledge();
//
//                } catch (Exception e) {
//                    log.error("Error processing message for chat room {}: {}", chatRoomName, e.getMessage(), e);
//                    throw new RuntimeException("KafkaConsumerService: Failed to process message for chat room: " + chatRoomName, e);
//                }
//            });
//
//            container.setConcurrency(3);
//            container.start();
//            return container;
//        });
    }

    public void stopListener(String chatRoomName) {
//        ConcurrentMessageListenerContainer<String, ChatRoomMessage> container = listenerContainers.remove(chatRoomName);
//        if (container != null)
//            container.stop();
    }

    public boolean isListenerRunning(String chatRoomName) {
        return listenerContainers.containsKey(chatRoomName);
    }
}
