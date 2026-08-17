package com.techmind.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${python.api.url}")
    private String pythonApiUrl;

    @Bean
    public RestClient pythonRestClient() {
        return RestClient.builder()
                .baseUrl(pythonApiUrl)
                .build();
    }
}
