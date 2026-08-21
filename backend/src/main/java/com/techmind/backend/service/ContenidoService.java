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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ContenidoService {

    private final ContenidoRepository contenidoRepository;

    public ContenidoService(ContenidoRepository contenidoRepository) {
        this.contenidoRepository = contenidoRepository;
    }

    @Transactional
    public void guardarContenidoYPrediccion(DataScienceRequestDto requestDto, DataScienceResponseDto responseDto) {
        // 1. Creamos la entidad Contenido con los datos de entrada
        Contenido contenido = new Contenido();
        contenido.setTitle(requestDto.title());
        contenido.setText(requestDto.text());

        // 2. Creamos la entidad Prediccion con la respuesta de Data Science
        Prediccion prediccion = new Prediccion();
        prediccion.setCategory(responseDto.category());
        prediccion.setConfidence(responseDto.confidence());

        // 3. Mapeamos la lista de cadenas ["html", "css", ...] a entidades Keyword
        if (responseDto.keywords() != null) {
            for (String kwText : responseDto.keywords()) {
                Keyword keyword = new Keyword(kwText);
                prediccion.addKeyword(keyword);
                // addKeyword hace internamente: keywords.add(keyword) Y keyword.setPrediccion(this)
            }
        }

        // 4. Mapeamos la lista de RecomendacionDto a entidades Recomendacion
        if (responseDto.recommendations() != null) {
            for (RecomendacionDTO recDto : responseDto.recommendations()) {
                Recomendacion recomendacion = new Recomendacion(
                        recDto.id(),
                        recDto.title(),
                        recDto.categoryRecs(),
                        recDto.type(),
                        recDto.level(),
                        recDto.language(),
                        recDto.url()
                );
                prediccion.addRecomendacion(recomendacion);
            }
        }

        // 5. Vinculamos ambas entidades
        prediccion.setContenido(contenido);
        contenido.setPrediccion(prediccion);

        // Guardar en Cascada
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
        contenidoRepository.desactivarRecomendacionesPorContenidoId(id);
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
        contenidoRepository.restaurarRecomendacionesPorContenidoId(id);
    }
}
