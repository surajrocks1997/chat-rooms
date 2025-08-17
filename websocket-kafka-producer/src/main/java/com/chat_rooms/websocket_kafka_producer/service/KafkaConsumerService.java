package com.chat_rooms.websocket_kafka_producer.service;

import com.chat_rooms.websocket_kafka_producer.dto.ChatRoomMessage;
import com.chat_rooms.websocket_kafka_producer.utility.RedisKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.ConsumerFactory;
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

    private final WebSocketSubscriberService subscriberService;
    private final JsonRedisService jsonRedisService;

    private final ConsumerFactory<String, ChatRoomMessage> chatRoomEventConsumerFactory;
    private final Map<String, ConcurrentMessageListenerContainer<String, ChatRoomMessage>> listenerContainers = new ConcurrentHashMap<>();

    public void startListener(String chatRoomName) {
        listenerContainers.computeIfAbsent(chatRoomName, name -> {
            ContainerProperties containerProps = new ContainerProperties("chat-room-topic-" + name);
            containerProps.setGroupId("chat-room-consumer-" + name);
            containerProps.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

            ConcurrentMessageListenerContainer<String, ChatRoomMessage> container = new ConcurrentMessageListenerContainer<>(chatRoomEventConsumerFactory, containerProps);
            container.setupMessageListener((AcknowledgingMessageListener<String, ChatRoomMessage>) (record, ack) -> {
                try {
                    jsonRedisService.publish(RedisKeys.BASE + record.value().getChatRoomName().getValue(), record.value());
                    ack.acknowledge();

                } catch (Exception e) {
                    log.error("Error processing message for chat room {}: {}", chatRoomName, e.getMessage(), e);
                }
            });

            container.setConcurrency(3);
            container.start();
            return container;
        });
    }

    public void stopListener(String chatRoomName) {
        ConcurrentMessageListenerContainer<String, ChatRoomMessage> container = listenerContainers.remove(chatRoomName);
        if (container != null)
            container.stop();
    }

    public boolean isListenerRunning(String chatRoomName) {
        return listenerContainers.containsKey(chatRoomName);
    }
}
