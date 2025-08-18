package com.chat_rooms.kafka_consumer_processor.service;

import com.chat_rooms.kafka_consumer_processor.dto.ChatRoomMessage;
import com.chat_rooms.kafka_consumer_processor.utils.RedisKeys;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomEventConsumerService {

    private final JsonRedisService jsonRedisService;

    @KafkaListener(topicPattern = "chat-room-topic-.*", groupId = "chat-room-consumer", containerFactory = "chatRoomEventListenerContainerFactory")
    public void listen(ConsumerRecord<String, ChatRoomMessage> record) throws JsonProcessingException {
        log.info("ChatRoomEventConsumerService : Received message for chat room: {}", record.value().getChatRoomName().getValue());
        jsonRedisService.publish(RedisKeys.BASE + record.value().getChatRoomName().getValue(), record.value());
        log.info("ChatRoomEventConsumerService : Published message for chat room: {} to Redis Topic Channel", record.value().getChatRoomName().getValue());
    }
}
