package com.techmind.backend.service;

import com.techmind.backend.dto.ContenidoRequest;
import com.techmind.backend.dto.PrediccionResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ClasificadorService {

    private final RestTemplate restTemplate;

    public ClasificadorService() {
        this.restTemplate = new RestTemplate();
    }

    public PrediccionResponse obtenerClasificacion(ContenidoRequest request) {
        String urlPython = "http://localhost:8888/predict";

        try {
            return restTemplate.postForObject(urlPython, request, PrediccionResponse.class);
        } catch (Exception e) {
            System.err.println("Error conectando con el servicio de Data Science: " + e.getMessage());
            return new PrediccionResponse("Servicio de IA no disponible", 0.0);
        }
    }
}
