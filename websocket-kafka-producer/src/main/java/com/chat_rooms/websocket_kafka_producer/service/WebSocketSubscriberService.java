package com.chat_rooms.websocket_kafka_producer.service;

import com.chat_rooms.websocket_kafka_producer.dto.ChatRoomMessage;
import com.chat_rooms.websocket_kafka_producer.dto.ChatRoomName;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketSubscriberService {

    private final SimpMessageSendingOperations messageTemplate;

    public void sendToSubscriber(ChatRoomMessage message, ChatRoomName chatRoomName) {
        this.messageTemplate.convertAndSend("/topic/chatRoom." + chatRoomName.getValue(), message);
    }

//    public void sendToUser(PrivateChatMessage message){
//        //        /user/{receiver}/queue/messages
//        this.messageTemplate.convertAndSendToUser(message.getReceiver(), "/queue/messages", message);
//    }
}
