package com.techmind.backend.service;

import com.techmind.backend.dto.ContenidoResponseDto;
import com.techmind.backend.dto.DataScienceRequestDto;
import com.techmind.backend.dto.DataScienceResponseDto;
import com.techmind.backend.dto.RecomendacionDTO;
import com.techmind.backend.entity.Contenido;
import com.techmind.backend.entity.Keyword;
import com.techmind.backend.entity.Prediccion;
import com.techmind.backend.entity.Recomendacion;
import com.techmind.backend.exception.ResourceNotFoundException;
import com.techmind.backend.repository.ContenidoRepository;
import com.techmind.backend.repository.RecommendationsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ContenidoService {

    private final ContenidoRepository contenidoRepository;
    private final RecommendationsRepository recommendationsRepository;

    public ContenidoService(ContenidoRepository contenidoRepository, RecommendationsRepository recommendationsRepository) {
        this.contenidoRepository = contenidoRepository;
        this.recommendationsRepository = recommendationsRepository;
    }

    @Transactional
    public void guardarContenidoYPrediccion(DataScienceRequestDto requestDto, DataScienceResponseDto responseDto) {
        // 1. Crear la entidad Contenido
        Contenido contenido = new Contenido();
        contenido.setTitle(requestDto.title());
        contenido.setText(requestDto.text());

        // 2. Crear la entidad Prediccion PRIMERO
        Prediccion prediccion = new Prediccion();
        prediccion.setCategory(responseDto.category());
        prediccion.setConfidence(responseDto.confidence());

        // 3. Mapear Keywords
        if (responseDto.keywords() != null) {
            for (String kwText : responseDto.keywords()) {
                Keyword keyword = new Keyword(kwText);
                prediccion.addKeyword(keyword);
            }
        }

        // 4. Mapear Recomendaciones (ahora 'prediccion' ya existe arriba)
        if (responseDto.recommendations() != null) {
            java.util.Set<Long> idsProcesados = new java.util.HashSet<>();

            for (RecomendacionDTO recDto : responseDto.recommendations()) {
                if (recDto.id() == null || idsProcesados.contains(recDto.id())) {
                    continue;
                }
                idsProcesados.add(recDto.id());

                Recomendacion recomendacionEntity = recommendationsRepository.findByExternalId(recDto.id())
                        .orElseGet(() -> {
                            Recomendacion nueva = new Recomendacion();
                            nueva.setExternalId(recDto.id());
                            nueva.setTitle(recDto.title() != null ? recDto.title() : "Sin título");
                            nueva.setCategoryRecs(recDto.categoryRecs() != null ? recDto.categoryRecs() : "General");
                            nueva.setType(recDto.type() != null ? recDto.type() : "General");
                            nueva.setLevel(recDto.level() != null ? recDto.level() : "N/A");
                            nueva.setLanguage(recDto.language() != null ? recDto.language() : "es");
                            nueva.setUrl(recDto.url() != null ? recDto.url() : "");
                            nueva.setActivo(true);

                            return recommendationsRepository.saveAndFlush(nueva);
                        });

                prediccion.addRecomendacion(recomendacionEntity);
            }
        }

        // 5. Vincular y Guardar en Cascada
        prediccion.setContenido(contenido);
        contenido.setPrediccion(prediccion);

        contenidoRepository.save(contenido);
    }

    @Transactional(readOnly = true)
    public List<ContenidoResponseDto> obtenerTodos() {
        return contenidoRepository.findAll().stream()
                .map(ContenidoResponseDto::deEntidad)
                .toList();
    }

    @Transactional(readOnly = true)
    public ContenidoResponseDto obtenerPorId(Long id) {
        return contenidoRepository.findById(id)
                .filter(Contenido::getActivo)
                .map(ContenidoResponseDto::deEntidad)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la recomendación con el ID: " + id));
    }

    @Transactional
    public void eliminarContenido(Long id) {
        // 1. Desactivar el contenido principal
        int filasAfectadas = contenidoRepository.desactivarContenidoPorId(id);

        if (filasAfectadas == 0) {
            throw new ResourceNotFoundException("El contenido con ID " + id + " no existe.");
        }

        // 2. Desactivar en cascada su predicción, keywords y recomendaciones
        contenidoRepository.desactivarPrediccionPorContenidoId(id);
        contenidoRepository.desactivarKeywordsPorContenidoId(id);
        // NOTA: Se elimina desactivarRecomendacionesPorContenidoId porque es un catálogo compartido contenidoRepository.desactivarRecomendacionesPorContenidoId(id);
    }

    @Transactional
    public void restaurarContenido(Long id) {
        // 1. Restaurar el registro principal en la tabla contenido
        int filasAfectadas = contenidoRepository.restaurarContenidoPorId(id);

        if (filasAfectadas == 0) {
            throw new ResourceNotFoundException("No se encontró el contenido con ID " + id + " para restaurar.");
        }

        // 2. Restaurar la predicción y palabras clave en cascada
        contenidoRepository.restaurarPrediccionPorContenidoId(id);
        contenidoRepository.restaurarKeywordsPorContenidoId(id);
        // contenidoRepository.restaurarRecomendacionesPorContenidoId(id);
    }
}
