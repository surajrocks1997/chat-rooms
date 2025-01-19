package com.chat_rooms.websocket_kafka_producer.controller;

import com.chat_rooms.websocket_kafka_producer.eventListener.ServerInfoListener;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final ServerInfoListener serverInfoListener;

    @GetMapping("/test")
    public ServerInfoListener getServerInfo() {
        return this.serverInfoListener;
    }
}
