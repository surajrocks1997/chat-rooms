package com.chat_rooms.websocket_kafka_producer.config;

import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final AuthServerConfigProperties authServerConfigProperties;

    @Bean
    public UriComponentsBuilder authServerApiUriBuilder() {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(authServerConfigProperties.getHost())
                .port(authServerConfigProperties.getPort());
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .requestFactory(this::clientHttpRequestFactory)
                .build();
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnPerRoute(20)
                .setMaxConnTotal(200)
                .build();

        HttpRequestRetryStrategy retryStrategy = new DefaultHttpRequestRetryStrategy(3, TimeValue.of(Duration.ofSeconds(3)));

        RequestConfig requestConfig = RequestConfig.custom()
                .setResponseTimeout(10L, TimeUnit.SECONDS)
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setRetryStrategy(retryStrategy)
                .setDefaultRequestConfig(requestConfig)
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }
}
