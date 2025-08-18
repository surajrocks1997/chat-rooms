package com.chat_rooms.websocket_kafka_producer.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.chat_rooms.websocket_kafka_producer.dto.RedisSubscriberChangedEvent;
import com.chat_rooms.websocket_kafka_producer.dto.RoomPresenceChangedEvent;
import com.chat_rooms.websocket_kafka_producer.dto.UserMetadata;
import com.chat_rooms.websocket_kafka_producer.eventListener.ServerInfoListener;
import com.chat_rooms.websocket_kafka_producer.security.UserRoleDetails;
import com.chat_rooms.websocket_kafka_producer.service.AuthServerService;
import com.chat_rooms.websocket_kafka_producer.service.JsonRedisService;
import com.chat_rooms.websocket_kafka_producer.service.RedisService;
import com.chat_rooms.websocket_kafka_producer.utils.RedisKeys;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PresenceInterceptor implements ChannelInterceptor {

    private final AuthServerService authServerService;
    private final RedisService redisService;
    private final JsonRedisService jsonRedisService;
    private final ServerInfoListener serverInfoListener;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        String sessionId = accessor.getSessionId();
        UserRoleDetails user = (UserRoleDetails) accessor.getUser();

        switch (accessor.getCommand()) {
            case CONNECT:
                log.info("preSend: CONNECT Command: Started");
                String bearerToken = accessor.getNativeHeader("Authorization") != null ? accessor.getNativeHeader("Authorization").getFirst() : null;
                if (bearerToken != null) {
                    try {
                        authServerService.validateToken(bearerToken);
                    } catch (Exception e) {
                        log.error("Invalid Token: {}", e.getMessage());
                        throw new IllegalArgumentException("Invalid Token");
                    }

                    DecodedJWT decodedJWT = JWT.decode(bearerToken.substring(7));
                    UserRoleDetails userRoleDetails = new UserRoleDetails(decodedJWT.getSubject(), decodedJWT.getClaim("id").asLong());
                    accessor.setUser(userRoleDetails);
                }
                // If the Authorization header is not present, log an error and throw an exception
                else {
                    log.error("Authorization header is missing in WebSocket connection request");
                    throw new IllegalArgumentException("Authorization header is missing");
                }

                user = (UserRoleDetails) accessor.getUser();

                try {
                    addPresenceWithSessionToUserMetadataToRedis(sessionId, user);
                } catch (JsonProcessingException e) {
                    log.error("preSend: CONNECT Command: Error while adding presence with session to user metadata in Redis: {}", e.getMessage());
                    throw new RuntimeException(e);
                }
                addPresenceWithUserIdToSessionIdToRedis(sessionId, user.getId());

                log.info("From Presence Interceptor CONNECT Command. SessionId: {}, UserId: {}", sessionId, user != null ? user.getId() : "Anonymous");
                log.info("preSend: CONNECT Command: Ended");
                break;

            case CONNECTED:
                log.info("preSend: CONNECTED Command: Started");
                log.info("From Presence Interceptor CONNECTED Command. SessionId: {}, UserId: {}", sessionId, user != null ? user.getId() : "Anonymous");
                log.info("preSend: CONNECTED Command: Ended");
                break;

            case SUBSCRIBE:
                log.info("preSend: SUBSCRIBE Command: Started");
                String accessorSubscriptionId = accessor.getSubscriptionId();
                eventPublisher.publishEvent(new RoomPresenceChangedEvent(accessorSubscriptionId, sessionId, true));
                eventPublisher.publishEvent(new RedisSubscriberChangedEvent(accessorSubscriptionId, true));

                addPresenceWithSessionIdToChatRoomToRedis(sessionId, accessorSubscriptionId);
                addPresenceWithChatRoomToSessionIdToRedis(accessorSubscriptionId, sessionId);


                log.info("From Presence Interceptor SUBSCRIBE Command. SessionId: {}, UserId: {}", sessionId, user != null ? user.getId() : "Anonymous");
                log.info("preSend: SUBSCRIBE Command: Ended");
                break;

            case UNSUBSCRIBE:
                log.info("preSend: UNSUBSCRIBE Command: Started");

                unsubscribeCleanUp(redisService.get(RedisKeys.PRESENCE_SESSION_TO_ROOM + sessionId), sessionId);

                log.info("From Presence Interceptor UNSUBSCRIBE Command. SessionId: {}, UserId: {}", sessionId, user != null ? user.getId() : "Anonymous");
                log.info("preSend: UNSUBSCRIBE Command: Ended");
                break;

            case DISCONNECT:
                log.info("preSend: DISCONNECT Command: Started");
                String room = redisService.get(RedisKeys.PRESENCE_SESSION_TO_ROOM + sessionId);
                if (room != null) {
                    unsubscribeCleanUp(room, sessionId);
                }

                removePresenceWithSessionToUserMetadataFromRedis(sessionId);
                removePresenceWithUserIdToSessionFromRedis(sessionId, user);
                log.info("From Presence Interceptor DISCONNECT Command. SessionId: {}, UserId: {}", sessionId, user != null ? user.getId() : "Anonymous");
                log.info("preSend: DISCONNECT Command: Ended");
                break;

            default:
                log.info("preSend: OTHER Command: Started");
                log.info("From Presence Interceptor OTHER Command : {}. SessionId: {}, UserId: {}", accessor.getCommand(), sessionId, user != null ? user.getId() : "Anonymous");
                log.info("preSend: OTHER Command: Ended");
                break;
        }


        return message;
    }

    private void unsubscribeCleanUp(String room, String sessionId) {
        eventPublisher.publishEvent(new RedisSubscriberChangedEvent(room, false));

        removePresenceWithChatRoomToSessionIdFromRedis(room, sessionId);
        removePresenceWithSessionIdToChatRoomFromRedis(sessionId);

        eventPublisher.publishEvent(new RoomPresenceChangedEvent(room, sessionId, false));
    }

    private void removePresenceWithChatRoomToSessionIdFromRedis(String roomId, String sessionId) {
        redisService.removeFromSet(RedisKeys.PRESENCE_ROOM_TO_SESSION + roomId, sessionId);
    }

    private void removePresenceWithSessionIdToChatRoomFromRedis(String sessionId) {
        redisService.deleteValueOps(RedisKeys.PRESENCE_SESSION_TO_ROOM + sessionId);
    }

    private void addPresenceWithChatRoomToSessionIdToRedis(String roomId, String sessionId) {
        redisService.addToSet(RedisKeys.PRESENCE_ROOM_TO_SESSION + roomId, sessionId);
    }

    private void addPresenceWithSessionIdToChatRoomToRedis(String sessionId, String roomId) {
        redisService.set(RedisKeys.PRESENCE_SESSION_TO_ROOM + sessionId, roomId);
    }

    private void removePresenceWithUserIdToSessionFromRedis(String sessionId, UserRoleDetails user) {
        redisService.deleteValueOps(RedisKeys.PRESENCE_USERID_TO_SESSIONID + user.getId());
    }

    private void addPresenceWithUserIdToSessionIdToRedis(String sessionId, Long id) {
        redisService.set(RedisKeys.PRESENCE_USERID_TO_SESSIONID + id, sessionId);
    }

    private void removePresenceWithSessionToUserMetadataFromRedis(String sessionId) {
        jsonRedisService.delete(RedisKeys.PRESENCE_SESSION_SESSIONID_TO_USERMETADATA + sessionId);
    }

    private void addPresenceWithSessionToUserMetadataToRedis(String sessionId, UserRoleDetails user) throws JsonProcessingException {
        jsonRedisService.set(
                RedisKeys.PRESENCE_SESSION_SESSIONID_TO_USERMETADATA + sessionId,
                UserMetadata.builder()
                        .userId(user.getId())
                        .email(user.getSub())
                        .connectedToServer(serverInfoListener.getHostName() + ":" + serverInfoListener.getPort())
                        .build()
        );
    }
}
