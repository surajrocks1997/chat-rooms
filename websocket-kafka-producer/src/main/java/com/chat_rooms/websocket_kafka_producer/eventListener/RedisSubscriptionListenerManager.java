package com.chat_rooms.websocket_kafka_producer.eventListener;

import com.chat_rooms.websocket_kafka_producer.dto.RedisSubscriberChangedEvent;
import com.chat_rooms.websocket_kafka_producer.dto.UserMetadata;
import com.chat_rooms.websocket_kafka_producer.service.JsonRedisService;
import com.chat_rooms.websocket_kafka_producer.service.RedisService;
import com.chat_rooms.websocket_kafka_producer.utils.RedisKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriptionListenerManager {

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final MessageListener messageListener;
    private final RedisService redisService;
    private final JsonRedisService jsonRedisService;
    private final ServerInfoListener serverInfoListener;

    @EventListener
    public void onRedisSubscriptionChanged(RedisSubscriberChangedEvent event) {
        if (event.hasSubscribed()) {
            log.info("RedisSubscriptionListenerManager: Subscription for Redis Topic room: {} : Adding", event.room());
            redisMessageListenerContainer.addMessageListener(messageListener, new ChannelTopic(RedisKeys.BASE + event.room()));
            log.info("RedisSubscriptionListenerManager: Subscription for Redis Topic room: {} : Added", event.room());
        } else {
            log.info("RedisSubscriptionListenerManager: Subscription for Redis Topic room: {} : Removing", event.room());

            // check if there are any users in the same server connected to this room
            // if not, remove the subscription
            List<UserMetadata> userMetadataList = new ArrayList<>();
            List<String> sessionIds = redisService.getSetValues(RedisKeys.PRESENCE_ROOM_TO_SESSION + event.room()).stream().toList();
            if (!sessionIds.isEmpty()) {
                List<String> keys = sessionIds.stream().map((sessionId) -> RedisKeys.PRESENCE_SESSION_SESSIONID_TO_USERMETADATA + sessionId).toList();
                userMetadataList = jsonRedisService.getAll(keys, UserMetadata.class)
                        .stream()
                        .filter((userMetadata) -> userMetadata.getConnectedToServer().equals(serverInfoListener.getServerInfo()))
                        .toList();
            }


            if (redisService.getSetValues(RedisKeys.PRESENCE_ROOM_TO_SESSION + event.room()).isEmpty() || userMetadataList.isEmpty()) {

                redisMessageListenerContainer.removeMessageListener(messageListener, new ChannelTopic(RedisKeys.BASE + event.room()));
                log.info("RedisSubscriptionListenerManager: Subscription for Redis Topic room: {} : Removed", event.room());
            } else {
                log.info("RedisSubscriptionListenerManager: Subscription for Redis Topic room: {} : Could Not Remove. Room on this Server {} is not empty",
                        serverInfoListener.getServerInfo(),
                        event.room());
            }
        }

    }
}
