package com.chat_rooms.websocket_kafka_producer.utils;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class UriBuilderUtil {

    public String buildUrl(UriComponentsBuilder baseUriBuilder, String path, @Nullable MultiValueMap<String, String> queryParams) {
        return UriComponentsBuilder
                .fromUriString(baseUriBuilder.toUriString())
                .path(path)
                .queryParams(queryParams)
                .toUriString();
    }
}
