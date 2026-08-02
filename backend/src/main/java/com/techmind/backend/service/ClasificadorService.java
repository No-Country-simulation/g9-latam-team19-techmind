package com.techmind.backend.service;

import com.techmind.backend.dto.ContenidoDTO;
import com.techmind.backend.dto.PrediccionDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ClasificadorService {

    private final RestTemplate restTemplate;

    public ClasificadorService() {
        this.restTemplate = new RestTemplate();
    }

    public PrediccionDTO obtenerClasificacion(ContenidoDTO request) {
        String urlPython = "http://localhost:8000/contenido";

        try {
            return restTemplate.postForObject(urlPython, request, PrediccionDTO.class);
        } catch (Exception e) {
            System.err.println("Error conectando con el servicio de Data Science: " + e.getMessage());
            return new PrediccionDTO("Servicio de IA no disponible", 0.0);
        }
    }
}
