package com.chat_rooms.websocket_kafka_producer.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.auth-server.request")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthServerConfigProperties {

    private String host;
    private String port;
}
