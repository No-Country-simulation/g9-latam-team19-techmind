package com.techmind.backend.service;

import com.techmind.backend.dto.DataScienceRequestDto;
import com.techmind.backend.dto.DataScienceResponseDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PythonApiClient {

    private final RestClient pythonRestClient;

    public PythonApiClient(RestClient pythonRestClient) {
        this.pythonRestClient = pythonRestClient;
    }

    public DataScienceResponseDto obtenerPrediccion(DataScienceRequestDto requestDto) {
        return pythonRestClient.post()
                .uri("/contenido")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestDto)
                .retrieve()
                .body(DataScienceResponseDto.class);
    }
}
