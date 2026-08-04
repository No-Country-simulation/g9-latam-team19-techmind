package com.techmind.backend.service;

import com.techmind.backend.dto.ContenidoResponseDto;
import com.techmind.backend.dto.DataScienceRequestDto;
import com.techmind.backend.dto.DataScienceResponseDto;
import com.techmind.backend.entity.Contenido;
import com.techmind.backend.entity.Keyword;
import com.techmind.backend.entity.Prediccion;
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

        // 4. Vinculamos ambas entidades
        prediccion.setContenido(contenido);
        contenido.setPrediccion(prediccion);

        // Guardar
        contenidoRepository.save(contenido);
    }

    @Transactional(readOnly = true)
    public List<ContenidoResponseDto> obtenerTodos() {
        return contenidoRepository.findAll().stream()
                .map(ContenidoResponseDto::deEntidad)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ContenidoResponseDto> obtenerPorId(Long id) {
        return contenidoRepository.findById(id)
                .map(ContenidoResponseDto::deEntidad);
    }
}
