package com.chat_rooms.kafka_consumer_processor.service;

import com.chat_rooms.kafka_consumer_processor.dto.ChatRoomMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserEventConsumerService {

    @KafkaListener(topics = "chat-room-topic", groupId = "user-event-consumer-group", containerFactory = "userEventKafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String, ChatRoomMessage> record){
        log.info("From ChatRoomMessage");
    }
}
