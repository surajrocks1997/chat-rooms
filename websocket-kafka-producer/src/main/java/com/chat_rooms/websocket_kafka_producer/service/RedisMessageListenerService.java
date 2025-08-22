package com.chat_rooms.websocket_kafka_producer.service;

import com.chat_rooms.websocket_kafka_producer.dto.ChatRoomMessage;
import com.chat_rooms.websocket_kafka_producer.dto.ChatRoomName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisMessageListenerService implements MessageListener {

    private final WebSocketSubscriberService webSocketSubscriberService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());
        try {
            ChatRoomMessage chatRoomMessage = objectMapper.readValue(body, ChatRoomMessage.class);
            ChatRoomName chatRoomName = ChatRoomName.valueOf(channel.substring(channel.lastIndexOf(":") + 1));
            log.info("RedisMessageListener: Received message for chat room {}: {}", chatRoomName, chatRoomMessage);
            webSocketSubscriberService.sendToSubscriber(chatRoomMessage, chatRoomName);
        } catch (JsonProcessingException e) {
            log.error("RedisMessageListener: Error parsing message: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
