package com.chat_rooms.auth_handler.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.google-api.auth")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GoogleConfigurationProperties {

    private String host;
    private String clientId;
    private String clientSecret;
    private String redirect_uri;

}
