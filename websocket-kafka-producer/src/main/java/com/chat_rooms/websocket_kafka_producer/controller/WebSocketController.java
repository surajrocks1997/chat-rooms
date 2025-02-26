package com.chat_rooms.websocket_kafka_producer.controller;

import com.chat_rooms.websocket_kafka_producer.component.ActiveChatRooms;
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
    private final ActiveChatRooms activeChatRooms;

    @MessageMapping("/chatRoom/{chatRoomName}")
    public void getMessage(@Payload ChatRoomMessage message, @DestinationVariable String chatRoomName, SimpMessageHeaderAccessor headerAccessor) {
        log.info("getMessage flow started");
        UserRoleDetails user = (UserRoleDetails) headerAccessor.getUser();
        log.info(String.valueOf(user));

        switch (message.getMessageType()) {
            case USER_ONLINE -> activeChatRooms.addSubscriber(message.getChatRoomName().getValue(), headerAccessor.getSessionId());
            case USER_OFFLINE -> activeChatRooms.removeSubscriber(message.getChatRoomName().getValue(), headerAccessor.getSessionId());
        }

        kafkaProducerService.produceChatRoomMessage(message);
        log.info("getMessage flow ended");
    }
}
