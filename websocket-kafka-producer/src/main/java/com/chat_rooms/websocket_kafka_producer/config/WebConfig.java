package com.chat_rooms.websocket_kafka_producer.config;

import com.chat_rooms.websocket_kafka_producer.filters.JWTFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<JWTFilter> jwtFilterFilterRegistrationBean(JWTFilter jwtFilter) {
        FilterRegistrationBean<JWTFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(jwtFilter);
        registrationBean.addUrlPatterns("/api/user/*");
        return registrationBean;
    }
}
