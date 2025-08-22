package com.chat_rooms.websocket_kafka_producer.eventListener;

import com.chat_rooms.websocket_kafka_producer.dto.*;
import com.chat_rooms.websocket_kafka_producer.service.JsonRedisService;
import com.chat_rooms.websocket_kafka_producer.service.KafkaProducerService;
import com.chat_rooms.websocket_kafka_producer.utils.RedisKeys;
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
    private final KafkaProducerService kafkaProducerService;
    private final JsonRedisService jsonRedisService;

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
                            .build(),
                    "chat-room-topic-" + event.room()
            );
            log.info("KafkaRoomListenerManager: KafkaProducer : Message Type: USER_ONLINE : Sent");

            // produce a message to kafka to dynamically start listener if not already started
            kafkaProducerService.produceChatRoomMessage(
                    ChatRoomMessage
                            .builder()
                            .messageType(MessageType.START_LISTENER)
                            .chatRoomName(ChatRoomName.valueOf(event.room()))
                            .username("system")
                            .build(),
                    "chat-room-topic-room-update"
            );
            log.info("KafkaRoomListenerManager: KafkaProducer : Message Type: START_LISTENER : Sent");

        } else {
            // If the user has left the room, produce a USER_OFFLINE message
            kafkaProducerService.produceChatRoomMessage(
                    ChatRoomMessage
                            .builder()
                            .messageType(MessageType.USER_OFFLINE)
                            .username(userMetadata.getEmail())
                            .chatRoomName(ChatRoomName.valueOf(event.room()))
                            .build(),
                    "chat-room-topic-" + event.room()
            );
            log.info("KafkaRoomListenerManager: KafkaProducer : Message Type: USER_OFFLINE : Sent");

            kafkaProducerService.produceChatRoomMessage(
                    ChatRoomMessage
                            .builder()
                            .messageType(MessageType.STOP_LISTENER)
                            .chatRoomName(ChatRoomName.valueOf(event.room()))
                            .username("system")
                            .build(),
                    "chat-room-topic-room-update"
            );
            log.info("KafkaRoomListenerManager: KafkaProducer : Message Type: STOP_LISTENER : Sent");
        }
    }
}
