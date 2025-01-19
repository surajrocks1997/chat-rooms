package com.chat_rooms.websocket_kafka_producer.eventListener;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Getter
@Component
@Slf4j
public class ServerInfoListener implements ApplicationListener<ServletWebServerInitializedEvent> {

    private int port;
    private String hostName;

    @Override
    public void onApplicationEvent(ServletWebServerInitializedEvent event) {
        this.port = event.getSource().getPort();
        try {
            this.hostName = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Failed to Get Host Address", e);
        }

        log.info("{}:{}", this.getHostName(), this.getPort());
    }
}
