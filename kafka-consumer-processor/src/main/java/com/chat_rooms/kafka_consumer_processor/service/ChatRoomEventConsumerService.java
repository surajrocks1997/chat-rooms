package com.chat_rooms.kafka_consumer_processor.service;

import com.chat_rooms.kafka_consumer_processor.dto.ChatRoomMessage;
import com.chat_rooms.kafka_consumer_processor.dto.MessageType;
import com.chat_rooms.kafka_consumer_processor.utils.RedisKeys;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomEventConsumerService {

    private final DynamicTopicListenerService dynamicTopicListenerService;
    private final RedisService redisService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ConcurrentMap<String, ScheduledFuture<?>> toBeStopped = new ConcurrentHashMap<>();

    private static final Long STOP_DEBOUNCE = 10L;

    @KafkaListener(topicPattern = "chat-room-topic-room-update", groupId = "chat-room-consumer", containerFactory = "chatRoomEventListenerContainerFactory")
    public void listen(ConsumerRecord<String, ChatRoomMessage> record) {
        MessageType messageType = record.value().getMessageType();
        String room = record.value().getChatRoomName().getValue();

        if (MessageType.START_LISTENER.equals(messageType)) {
            ScheduledFuture<?> pending = toBeStopped.remove(room);

            if (pending != null)
                pending.cancel(false);

            if (!dynamicTopicListenerService.isListenerRunning(room)) {
                dynamicTopicListenerService.startListener(room);
                log.info("ChatRoomEventConsumerService: Started Kafka listener for room: {}", room);
            }
        } else if (MessageType.STOP_LISTENER.equals(messageType)) {
            if (toBeStopped.containsKey(room)) {
                log.info("ChatRoomEventConsumerService: Room {} is already scheduled for stop", room);
                return;
            }

            ScheduledFuture<?> scheduledFuture = scheduler.schedule(() -> {
                try {
                    if (redisService.isSetEmpty(RedisKeys.PRESENCE_ROOM_TO_SESSION + room)) {
                        log.info("ChatRoomEventConsumerService: Stopping Kafka listener for room: {}", room);
                        dynamicTopicListenerService.stopListener(room);
                        log.info("ChatRoomEventConsumerService: Stopped Kafka listener for room: {}", room);
                    } else {
                        log.info("ChatRoomEventConsumerService: Room {} is not empty, skipping stop", room);
                    }
                } finally {
                    toBeStopped.remove(room);
                    log.info("ChatRoomEventConsumerService: toBeStopped Cleanup completed for room: {}", room);
                }
            }, STOP_DEBOUNCE, TimeUnit.SECONDS);

            toBeStopped.put(room, scheduledFuture);
            log.info("KafkaRoomListenerManager: Scheduled stop for room: {} in {} seconds", room, STOP_DEBOUNCE);
        }
    }

    // Shuts down the scheduler when the application context is destroyed
    @PreDestroy
    public void shutDown() {
        scheduler.shutdownNow();
    }
}
