package com.techmind.backend.service;

import com.techmind.backend.dto.ContenidoDTO;
import com.techmind.backend.dto.PrediccionDTO;
import com.techmind.backend.dto.RecomendacionDTO;
import com.techmind.backend.entity.Contenido;
import com.techmind.backend.entity.Keyword;
import com.techmind.backend.entity.Prediccion;
import com.techmind.backend.entity.Recomendacion;
import com.techmind.backend.repository.ContenidoRepository;
import com.techmind.backend.repository.RecommendationsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class ClasificadorService {

    private final RestTemplate restTemplate;
    private final ContenidoRepository contenidoRepository;
    private final RecommendationsRepository recommendationsRepository;

    public ClasificadorService(RestTemplate restTemplate,
                               ContenidoRepository contenidoRepository,
                               RecommendationsRepository recommendationsRepository) {
        this.restTemplate = restTemplate;
        this.contenidoRepository = contenidoRepository;
        this.recommendationsRepository = recommendationsRepository;
    }


    @Transactional
    public Contenido procesarYGuardar(ContenidoDTO request) {
        String urlPython = "http://localhost:8000/contenido";

        // Contruir la API / Data Science
        PrediccionDTO responseDTO = restTemplate.postForObject(urlPython, request, PrediccionDTO.class);;

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

        // 4. Mapear recomendaciones evitando duplicados
        if (responseDTO.getRecommendations() != null) {
            java.util.Set<Long> idsProcesados = new java.util.HashSet<>();

            for (RecomendacionDTO recDto : responseDTO.getRecommendations()) {

                // Evita procesar duplicados que vengan repetidos en el mismo JSON de respuesta
                if (recDto.id() == null || idsProcesados.contains(recDto.id())) {
                    continue;
                }
                idsProcesados.add(recDto.id());

                // Buscar en BD
                java.util.Optional<Recomendacion> existente = recommendationsRepository.findByExternalId(recDto.id());

                Recomendacion recomendacionEntity;
                // Si el externalId existe en la BD se reutiliza; si no, se crea
                if (existente.isPresent()) {
                    // Si ya existe, usamos la entidad gestionada existente
                    recomendacionEntity = existente.get();
                } else {
                    // Si no existe, la creamos y la guardamos primero
                    Recomendacion nueva = new Recomendacion();
                    nueva.setExternalId(recDto.id());
                    nueva.setTitle(recDto.title() != null ? recDto.title() : "Sin título");
                    nueva.setCategoryRecs(recDto.categoryRecs() != null ? recDto.categoryRecs() : "General");
                    nueva.setType(recDto.type() != null ? recDto.type() : "General");
                    nueva.setLevel(recDto.level() != null ? recDto.level() : "N/A");
                    nueva.setLanguage(recDto.language() != null ? recDto.language() : "es");
                    nueva.setUrl(recDto.url() != null ? recDto.url() : "");
                    nueva.setActivo(true);

                    recomendacionEntity = recommendationsRepository.save(nueva);
                }
                // Asocia la recomendación a la predicción
                prediccion.addRecomendacion(recomendacionEntity);
            }
        }

        // 4. Guardar la Predicción en MySQL
        return contenidoRepository.save(contenido);
    }
}
