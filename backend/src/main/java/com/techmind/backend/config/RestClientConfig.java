package com.techmind.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${python.api.url}")
    private String pythonApiUrl;

    @Bean
    public RestClient pythonRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000); // 3 segundos para establecer conexion
        factory.setReadTimeout(5000);    // 5 segundos para recibir respuesta

        return RestClient.builder()
                .baseUrl(pythonApiUrl)
                .requestFactory(factory)
                .build();
    }
}
