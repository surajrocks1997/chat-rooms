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
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriptionListenerManager {

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final MessageListener messageListener;
    private final RedisService redisService;
    private final JsonRedisService jsonRedisService;
    private final ServerInfoListener serverInfoListener;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ConcurrentMap<String, ScheduledFuture<?>> toBeStopped = new ConcurrentHashMap<>();

    private static final Long STOP_DEBOUNCE = 10L;


    @EventListener
    public void onRedisSubscriptionChanged(RedisSubscriberChangedEvent event) {
        String room = RedisKeys.PRESENCE_ROOM_TO_SESSION + event.room();
        if (event.hasSubscribed()) {
            log.info("RedisSubscriptionListenerManager: Subscription for Redis Topic room: {} : Adding", event.room());

            ScheduledFuture<?> remove = toBeStopped.remove(room);
            if (remove != null) {
                log.info("RedisSubscriptionListenerManager: Subscription for Redis Topic room: {} : Cancelling scheduled stop", event.room());
                remove.cancel(false);
            }

            redisMessageListenerContainer.addMessageListener(messageListener, new ChannelTopic(RedisKeys.BASE + event.room()));
            log.info("RedisSubscriptionListenerManager: Subscription for Redis Topic room: {} : Added", event.room());

        } else {
            log.info("RedisSubscriptionListenerManager: Subscription for Redis Topic room: {} : Removing", event.room());

            ScheduledFuture<?> scheduledFuture = scheduler.schedule(() -> {
                // check if there are any users in the same server connected to this room
                // if not, remove the subscription
                try {
                    List<UserMetadata> userMetadataList = new ArrayList<>();
                    List<String> sessionIds = redisService.getSetValues(room).stream().toList();
                    if (!sessionIds.isEmpty()) {
                        List<String> keys = sessionIds.stream().map((sessionId) -> RedisKeys.PRESENCE_SESSION_SESSIONID_TO_USERMETADATA + sessionId).toList();
                        userMetadataList = jsonRedisService.getAll(keys, UserMetadata.class)
                                .stream()
                                .filter((userMetadata) -> userMetadata.getConnectedToServer().equals(serverInfoListener.getServerInfo()))
                                .toList();
                    }
                    if (redisService.getSetValues(room).isEmpty() || userMetadataList.isEmpty()) {

                        redisMessageListenerContainer.removeMessageListener(messageListener, new ChannelTopic(RedisKeys.BASE + event.room()));
                        log.info("RedisSubscriptionListenerManager: Subscription for Redis Topic room: {} : Removed", event.room());
                    } else {
                        log.info("RedisSubscriptionListenerManager: Subscription for Redis Topic room: {} : Could Not Remove. Room on this Server {} is not empty",
                                serverInfoListener.getServerInfo(),
                                event.room());
                    }
                } finally {
                    toBeStopped.remove(room);
                    log.info("RedisSubscriptionListenerManager: Subscription for Redis Topic room: {} : toBeStopped Cleanup completed", event.room());
                }

            }, STOP_DEBOUNCE, TimeUnit.SECONDS);

            toBeStopped.put(room, scheduledFuture);

        }

    }
}
