package com.chat_rooms.websocket_kafka_producer.controller;

import com.chat_rooms.websocket_kafka_producer.dto.UserInfoProjection;
import com.chat_rooms.websocket_kafka_producer.dto.UserMetadata;
import com.chat_rooms.websocket_kafka_producer.service.JsonRedisService;
import com.chat_rooms.websocket_kafka_producer.service.RedisService;
import com.chat_rooms.websocket_kafka_producer.service.UserService;
import com.chat_rooms.websocket_kafka_producer.utils.RedisKeys;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/api/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RedisService redisService;
    private final JsonRedisService jsonRedisService;

    @GetMapping
    public ResponseEntity<UserInfoProjection> getUser(HttpServletRequest request) {
        log.info("getUser flow started");
        UserInfoProjection user = userService.findUserById((Long) request.getAttribute("claimId"));


        log.info("getUser flow ended");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserInfoProjection> getUserByUsername(@PathVariable(name = "username") String username) {
        log.info("getUserByUsername flow started");
        UserInfoProjection user = userService.findUserByUsername(username);
        log.info("getUserByUsername flow started");

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/chatRooms/{chatRoomName}/online")
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
