package com.chat_rooms.websocket_kafka_producer.eventListener;

import com.chat_rooms.websocket_kafka_producer.dto.*;
import com.chat_rooms.websocket_kafka_producer.service.JsonRedisService;
import com.chat_rooms.websocket_kafka_producer.service.KafkaConsumerService;
import com.chat_rooms.websocket_kafka_producer.service.KafkaProducerService;
import com.chat_rooms.websocket_kafka_producer.service.RedisService;
import com.chat_rooms.websocket_kafka_producer.utility.RedisKeys;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaRoomListenerManager {
    private final KafkaConsumerService kafkaConsumerService;
    private final KafkaProducerService kafkaProducerService;
    private final RedisService redisService;
    private final JsonRedisService jsonRedisService;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ConcurrentMap<String, ScheduledFuture<?>> pendingStops = new ConcurrentHashMap<>();

    private static final Long STOP_DEBOUNCE = 5L;

    // Listens for room presence changes and manages Kafka listeners accordingly
    @EventListener
    public void onRoomPresenceChanged(RoomPresenceChangedEvent event) throws JsonProcessingException {
        String room = event.room();
        log.info("KafkaRoomListenerManager: Received RoomPresenceChangedEvent for room: {}", room);
        UserMetadata userMetadata = jsonRedisService.get(RedisKeys.PRESENCE_SESSION_SESSIONID_TO_USERMETADATA + event.sessionId(), UserMetadata.class);
        if (event.hasJoined()) {
            // If the user has joined the room, produce a USER_ONLINE message
            kafkaProducerService.produceChatRoomMessage(
                    ChatRoomMessage
                            .builder()
                            .messageType(MessageType.USER_ONLINE)
                            .username(userMetadata.getEmail())
                            .chatRoomName(ChatRoomName.valueOf(event.room()))
                            .build()
            );
            log.info("KafkaRoomListenerManager: KafkaProducer : Message Type: USER_ONLINE : Sent");
            ScheduledFuture<?> pending = pendingStops.remove(room);
            if (pending != null) pending.cancel(false);

            if (!kafkaConsumerService.isListenerRunning(room)) {
                kafkaConsumerService.startListener(room);
                log.info("KafkaRoomListenerManager: Started Kafka listener for room: {}", room);
            }
        } else {
            // If the user has left the room, produce a USER_OFFLINE message
            kafkaProducerService.produceChatRoomMessage(
                    ChatRoomMessage
                            .builder()
                            .messageType(MessageType.USER_OFFLINE)
                            .username(userMetadata.getEmail())
                            .chatRoomName(ChatRoomName.valueOf(event.room()))
                            .build()
            );
            log.info("KafkaRoomListenerManager: KafkaProducer : Message Type: USER_OFFLINE : Sent");
            if (room == null || room.isEmpty()) {
                log.warn("KafkaRoomListenerManager: Received empty room name in RoomPresenceChangedEvent");
                return;
            }
            if (pendingStops.containsKey(room)) {
                log.info("KafkaRoomListenerManager: Room {} is already scheduled for stop", room);
                return;
            }

            ScheduledFuture<?> scheduledFuture = scheduler.schedule(() -> {
                try {
                    if (redisService.isSetEmpty(RedisKeys.PRESENCE_ROOM_TO_SESSION + room) && kafkaConsumerService.isListenerRunning(room)) {
                        log.info("KafkaRoomListenerManager: Stopping Kafka listener for room: {}", room);
                        kafkaConsumerService.stopListener(room);
                        log.info("KafkaRoomListenerManager: Stopped Kafka listener for room: {}", room);
                    } else {
                        log.info("KafkaRoomListenerManager: Room {} is not empty, skipping stop", room);
                    }
                } finally {
                    pendingStops.remove(room);
                    log.info("KafkaRoomListenerManager: pendingStops Cleanup completed for room: {}", room);
                }
            }, STOP_DEBOUNCE, TimeUnit.SECONDS);

            pendingStops.put(room, scheduledFuture);
            log.info("KafkaRoomListenerManager: Scheduled stop for room: {} in {} seconds", room, STOP_DEBOUNCE);
        }
    }

    // Shuts down the scheduler when the application context is destroyed
    @PreDestroy
    public void shutDown() {
        scheduler.shutdownNow();
    }


}
