package com.chat_rooms.websocket_kafka_producer.service;

import com.chat_rooms.websocket_kafka_producer.dto.ChatRoomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, ChatRoomMessage> chatRoomEventKafkaTemplate;

    public void produceChatRoomMessage(ChatRoomMessage chatRoomMessage) {
        CompletableFuture<SendResult<String, ChatRoomMessage>> send = chatRoomEventKafkaTemplate.send(
                "chat-room-topic-" + chatRoomMessage.getChatRoomName().getValue(),
                chatRoomMessage.getUsername(),
                chatRoomMessage);

        send.whenComplete((res, ex) -> {
            if (ex != null)
                log.error("Failed To Send Message: {}", ex.getMessage());
        });
    }
}