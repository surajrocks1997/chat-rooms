package com.chat_rooms.websocket_kafka_producer.controller;

import com.chat_rooms.websocket_kafka_producer.dto.ChatRoomMessage;
import com.chat_rooms.websocket_kafka_producer.security.UserRoleDetails;
import com.chat_rooms.websocket_kafka_producer.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final KafkaProducerService kafkaProducerService;

    @MessageMapping("/chatRoom/{chatRoomName}")
    public void getMessage(@Payload ChatRoomMessage message, @DestinationVariable String chatRoomName, SimpMessageHeaderAccessor headerAccessor) {
        log.info("WebSocket Controller MessageMapping getMessage flow started");
        UserRoleDetails user = (UserRoleDetails) headerAccessor.getUser();
        log.info(String.valueOf(user));

        kafkaProducerService.produceChatRoomMessage(message, "chat-room-topic-" + message.getChatRoomName().getValue());
        log.info("WebSocket Controller MessageMapping getMessage flow ended");
    }
}
