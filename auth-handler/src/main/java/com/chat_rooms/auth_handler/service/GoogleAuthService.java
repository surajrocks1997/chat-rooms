package com.chat_rooms.auth_handler.service;

import com.chat_rooms.auth_handler.config.GoogleConfigurationProperties;
import com.chat_rooms.auth_handler.dto.GoogleTokenResponse;
import com.chat_rooms.auth_handler.dto.GoogleUserInfo;
import com.chat_rooms.auth_handler.utils.UriBuilderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    @Value("${app.google-api.host}")
    private String getProfileHost;

    private final UriComponentsBuilder googleApiUriBuilder;
    private final RestTemplate restTemplate;
    private final UriBuilderUtil uriBuilderUtil;
    private final GoogleConfigurationProperties googleConfigurationProperties;

    public GoogleTokenResponse getTokenDetail(String authCode) {
        log.info("getTokenDetail flow started");
        String url = uriBuilderUtil.buildUrl(googleApiUriBuilder, "/token", null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Content-Type", "application/x-www-form-urlencoded");


        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authCode);
        body.add("client_id", googleConfigurationProperties.getClientId());
        body.add("client_secret", googleConfigurationProperties.getClientSecret());
        body.add("redirect_uri", googleConfigurationProperties.getRedirect_uri());
        body.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<GoogleTokenResponse> exchange = restTemplate.exchange(url, HttpMethod.POST, requestEntity, GoogleTokenResponse.class);

        return exchange.getBody();
    }

    public GoogleUserInfo getUserInfo(String accessToken) {
        log.info("getUserInfo flow started");
        String url = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(getProfileHost)
                .path("/oauth2/v2/userinfo")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<GoogleUserInfo> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, GoogleUserInfo.class);
        return exchange.getBody();
    }
}
