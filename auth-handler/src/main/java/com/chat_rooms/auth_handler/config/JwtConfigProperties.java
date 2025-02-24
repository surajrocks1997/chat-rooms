package com.chat_rooms.auth_handler.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JwtConfigProperties {

    private String secret;
    private String aud;
    private String iss;
}
