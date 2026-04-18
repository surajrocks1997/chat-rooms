package com.chat_rooms.websocket_kafka_producer.controller;

import com.chat_rooms.websocket_kafka_producer.service.SocialService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/social")
@Slf4j
@RequiredArgsConstructor
public class SocialController {

    private final SocialService socialService;

    @PostMapping("/friend/request/{receiverId}")
    public ResponseEntity<Void> sendFriendRequest(HttpServletRequest request, @PathVariable Long receiverId) {
        log.info("FriendController : sendFriendRequest flow started");

        Long senderId = (Long) request.getAttribute("claimId");
        socialService.sendFriendRequest(senderId, receiverId);

        log.info("FriendController : sendFriendRequest flow ended");

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/friendship/accept/{id}")
    public ResponseEntity<Void> acceptFriendRequest(HttpServletRequest request, @PathVariable(name = "id") Long senderId) {
        log.info("SocialController : acceptFriendRequest flow started");

        Long userId = (Long) request.getAttribute("claimId");
        socialService.acceptFriendRequest(userId, senderId);

        log.info("SocialController : acceptFriendRequest flow ended");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Set<Long>>> getFriendsStatus(HttpServletRequest request) {
        log.info("FriendController: getSocialSummary flow started");
        Long senderId = (Long) request.getAttribute("claimId");
        Map<String, Set<Long>> socialSummary = socialService.getFriendStatus(senderId);

        log.info("FriendController: getSocialSummary flow ended");
        return new ResponseEntity<>(socialSummary, HttpStatus.OK);
    }

}
