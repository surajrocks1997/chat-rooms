package com.chat_rooms.websocket_kafka_producer.controller;

import com.chat_rooms.websocket_kafka_producer.dto.UserMetadata;
import com.chat_rooms.websocket_kafka_producer.service.JsonRedisService;
import com.chat_rooms.websocket_kafka_producer.service.RedisService;
import com.chat_rooms.websocket_kafka_producer.utils.RedisKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chatRooms")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final RedisService redisService;
    private final JsonRedisService jsonRedisService;

    @GetMapping("/{chatRoomName}/online")
    public ResponseEntity<List<String>> getAllOnlineUsersInChatRoom(@PathVariable String chatRoomName) {
        log.info("getAllOnlineUsersInChatRoom flow started");
        List<String> sessionIds = redisService.getSetValues(RedisKeys.PRESENCE_ROOM_TO_SESSION + chatRoomName).stream().toList();
        if (sessionIds.isEmpty())
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);

        List<String> keys = sessionIds.stream()
                .limit(10)
                .map((sessionId) -> RedisKeys.PRESENCE_SESSION_SESSIONID_TO_USERMETADATA + sessionId)
                .toList();

        List<UserMetadata> all = jsonRedisService.getAll(keys, UserMetadata.class);
        // return only usernames
        List<String> usernames = all.stream().map((UserMetadata::getEmail)).toList();
        log.info("getAllOnlineUsersInChatRoom flow ended");
        return new ResponseEntity<>(usernames, HttpStatus.OK);
    }
}
