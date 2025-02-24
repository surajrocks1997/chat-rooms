package com.chat_rooms.websocket_kafka_producer.service;

import com.chat_rooms.websocket_kafka_producer.utils.UriBuilderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServerService {

    private final RestTemplate restTemplate;
    private final UriComponentsBuilder authServerApiUriBuilder;
    private final UriBuilderUtil uriBuilderUtil;

    // takes full Bearer <TOKEN> as input
    public void validateToken(String bearerToken) {
        String url = uriBuilderUtil.buildUrl(authServerApiUriBuilder, "/api/auth/validateToken", null);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", bearerToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
    }

}
