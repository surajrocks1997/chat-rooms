package com.chat_rooms.kafka_consumer_processor.config;

import com.chat_rooms.kafka_consumer_processor.dto.ChatRoomMessage;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.RecordDeserializationException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ChatRoomEventConsumerConfig {

    @Autowired
    private Environment env;

    private final KafkaTemplate<String, Object> deadLetterEventKafkaTemplate;

    @Bean
    public ConsumerFactory<String, ChatRoomMessage> chatRoomEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("spring.kafka.bootstrap-servers"));
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.chat_rooms.websocket_kafka_producer.dto.ChatRoomMessage");

        JsonDeserializer<ChatRoomMessage> deserializer = new JsonDeserializer<>(ChatRoomMessage.class);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChatRoomMessage> chatRoomEventListenerContainerFactory(ConsumerFactory<String, ChatRoomMessage> chatRoomEventConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, ChatRoomMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(chatRoomEventConsumerFactory);
        factory.setAutoStartup(true);
        factory.setCommonErrorHandler(defaultErrorHandler());
        return factory;
    }

    private CommonErrorHandler defaultErrorHandler() {
        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer = new DeadLetterPublishingRecoverer(deadLetterEventKafkaTemplate,
                (record, exception) -> new TopicPartition("chat-room-dead-letter-topic", record.partition()));

        FixedBackOff fixedBackOff = new FixedBackOff(2000L, 3);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(deadLetterPublishingRecoverer, fixedBackOff);
        errorHandler.addNotRetryableExceptions(DeserializationException.class, RecordDeserializationException.class);

        return errorHandler;
    }

}
