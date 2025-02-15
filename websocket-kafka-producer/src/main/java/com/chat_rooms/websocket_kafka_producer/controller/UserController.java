package com.chat_rooms.websocket_kafka_producer.controller;

import com.chat_rooms.websocket_kafka_producer.dto.UserInfoProjection;
import com.chat_rooms.websocket_kafka_producer.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserInfoProjection> getUser(HttpServletRequest request) {
        log.info("getUser flow started");
        UserInfoProjection user = userService.findUserById((Long) request.getAttribute("claimId"));


        log.info("getUser flow ended");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
