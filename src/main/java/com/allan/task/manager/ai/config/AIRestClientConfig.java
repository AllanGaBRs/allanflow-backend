package com.allan.task.manager.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class AIRestClientConfig {

    @Bean
    public RestClient n8nRestClient(
            RestClient.Builder builder,
            @Value("${integration.n8n.chat-webhook}") String webhook,
            @Value("${integration.n8n.connect-timeout}") Duration connectTimeout,
            @Value("${integration.n8n.read-timeout}") Duration readTimeout
    ) {

        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);

        return builder
                .baseUrl(webhook)
                .requestFactory(requestFactory)
                .build();
    }

}