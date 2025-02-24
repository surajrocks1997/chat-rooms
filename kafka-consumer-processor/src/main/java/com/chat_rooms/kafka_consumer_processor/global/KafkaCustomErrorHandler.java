package com.chat_rooms.kafka_consumer_processor.global;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.RecordDeserializationException;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
public class KafkaCustomErrorHandler extends DefaultErrorHandler {

    public KafkaCustomErrorHandler() {
        super(new FixedBackOff(2000L, 3));
    }

    @Override
    public boolean handleOne(Exception thrownException, ConsumerRecord<?, ?> record, Consumer<?, ?> consumer, MessageListenerContainer container) {
        if(thrownException instanceof DeserializationException) {
            log.error("Deserialization Error for Record with key {}: {}", record.key(), thrownException.getMessage());
            handle(thrownException, consumer);
        }

        return seeksAfterHandling();
    }

    @Override
    public void handleOtherException(Exception thrownException, Consumer<?, ?> consumer, MessageListenerContainer container, boolean batchListener) {
        log.error("Other Consumer Exception Occured : {}", thrownException.getMessage());
        handle(thrownException, consumer);
        super.handleOtherException(thrownException, consumer, container, batchListener);
    }

    private void handle(Exception exception, Consumer<?, ?> consumer) {
        log.error("Custom Handling for consumer error");

        if (exception instanceof RecordDeserializationException ex) {
            consumer.seek(ex.topicPartition(), ex.offset() + 1L);
            consumer.commitSync();
        } else {
            log.error("Exception not handled: ", exception);
        }
    }
}
