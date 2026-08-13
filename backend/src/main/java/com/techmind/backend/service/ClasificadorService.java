package com.techmind.backend.service;

import com.techmind.backend.dto.ContenidoDTO;
import com.techmind.backend.dto.PrediccionDTO;
import com.techmind.backend.entity.Contenido;
import com.techmind.backend.entity.Keyword;
import com.techmind.backend.entity.Prediccion;
import com.techmind.backend.repository.ContenidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class ClasificadorService {

    private final RestTemplate restTemplate;
    private final ContenidoRepository contenidoRepository;

    public ClasificadorService(RestTemplate restTemplate, ContenidoRepository contenidoRepository) {
        this.restTemplate = restTemplate;
        this.contenidoRepository = contenidoRepository;
    }


    @Transactional
    public Contenido procesarYGuardar(ContenidoDTO request) {
        String urlPython = "http://localhost:8000/contenido";

        // Contruir la API / Data Science
        PrediccionDTO responseDTO;
        try {
            responseDTO = restTemplate.postForObject(urlPython, request, PrediccionDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("La respuesta del servicio de IA fallo.");
        }

        // 1. Instanciamos Contenido
        Contenido contenido = new Contenido();
        contenido.setTitle(request.getTitle());
        contenido.setText(request.getText());

        // 2. Instanciamos Prediccion
        Prediccion prediccion = new Prediccion();
        prediccion.setCategory(responseDTO.getCategory());
        prediccion.setConfidence(responseDTO.getConfidence());

        // Se asigna la relacion OneToOne (Prediccion -> Contenido)
        prediccion.setContenido(contenido);

        // Y en sentido inverso
        contenido.setPrediccion(prediccion);

        // 3. Mapear las keywords (List<String>) a objetos entidad Keyword
        if (responseDTO.getKeywords() != null) {
            for (String kwStr : responseDTO.getKeywords()) {
                Keyword keywordEntity = new Keyword(kwStr);

                // addKeyword asocia el keyword a la lista Y le asigna la prediccion (prediccion_id)
                prediccion.addKeyword(keywordEntity);
            }
        }

        // 4. Guardar la Predicción en MySQL
        return contenidoRepository.save(contenido);
    }
}
