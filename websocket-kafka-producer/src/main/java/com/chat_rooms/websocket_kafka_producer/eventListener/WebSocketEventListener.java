package com.chat_rooms.websocket_kafka_producer.eventListener;

import com.chat_rooms.websocket_kafka_producer.security.UserRoleDetails;
import com.chat_rooms.websocket_kafka_producer.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final RedisService redisService;
    private final ServerInfoListener serverInfoListener;

    private static final String USER_TO_SERVER_MAP_HASH_KEY = "userToServerMap";

    @EventListener
    public void handleWebSocketConnectEvent(SessionConnectEvent event) {
        UserRoleDetails userIdFromPrincipal = getUserIdFromPrincipal(event.getUser());
        if (userIdFromPrincipal != null) {
            log.info("From Session Connect Event. UserId: {}", userIdFromPrincipal.getId());
        }
    }

    @EventListener
    public void handleWebSocketConnectedEvent(SessionConnectedEvent event) {
        UserRoleDetails userIdFromPrincipal = getUserIdFromPrincipal(event.getUser());
        if (userIdFromPrincipal != null) {
            log.info("From Session Connected Event. UserId: {}", userIdFromPrincipal.getId());
            String key = "chatRooms:userToServerMap:" + userIdFromPrincipal.getId();
            String server = serverInfoListener.getHostName() + ":" + serverInfoListener.getPort();
            redisService.set(key, server);
        }

    }

    @EventListener
    public void handleWebSocketDisconnectEvent(SessionDisconnectEvent event) {
        UserRoleDetails userIdFromPrincipal = getUserIdFromPrincipal(event.getUser());
        if (userIdFromPrincipal != null) {
            log.info("From Session Disconnect Event. UserId: {}", userIdFromPrincipal.getId());
            String key = "chatRooms:userToServerMap:" + userIdFromPrincipal.getId();
            String server = serverInfoListener.getHostName() + ":" + serverInfoListener.getPort();
            redisService.deleteValueOps(key);
        }
    }

    private UserRoleDetails getUserIdFromPrincipal(Principal principal) {
        if (principal instanceof UserRoleDetails)
            return (UserRoleDetails) principal;
        return null;
    }

}
