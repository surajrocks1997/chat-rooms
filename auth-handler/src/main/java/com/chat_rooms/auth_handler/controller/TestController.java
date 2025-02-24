package com.chat_rooms.auth_handler.controller;

import com.chat_rooms.auth_handler.utils.UriBuilderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final UriComponentsBuilder testApiUriBuilder;
    private final RestTemplate restTemplate;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping("/test")
    public ResponseEntity<Object> getReqresData() {

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page", "2");
        String url = uriBuilderUtil.buildUrl(testApiUriBuilder, "/users", queryParams);


        ResponseEntity<Object> exchange = restTemplate.exchange(url, HttpMethod.GET, null, Object.class);

        return new ResponseEntity<>(exchange.getBody(), exchange.getStatusCode());
    }
}
