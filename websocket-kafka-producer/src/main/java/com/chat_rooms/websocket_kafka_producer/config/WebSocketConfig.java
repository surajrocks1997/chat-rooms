package com.chat_rooms.websocket_kafka_producer.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.chat_rooms.websocket_kafka_producer.security.UserRoleDetails;
import com.chat_rooms.websocket_kafka_producer.service.AuthServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Objects;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private Environment env;

    private final AuthServerService authServerService;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/chat-rooms")
                .setAllowedOrigins(env.getProperty("app.cors.allowed-origins"))
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String bearerToken = Objects.requireNonNull(accessor.getNativeHeader("Authorization")).getFirst();

                    try {
                        authServerService.validateToken(bearerToken);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        throw new IllegalArgumentException("Invalid Token");
                    }

                    DecodedJWT decodedJWT = JWT.decode(bearerToken.substring(7));

                    UserRoleDetails userRoleDetails = new UserRoleDetails(decodedJWT.getSubject(), decodedJWT.getClaim("id").asLong());
                    accessor.setUser(userRoleDetails);

                }

                return message;
            }

        });
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
}
