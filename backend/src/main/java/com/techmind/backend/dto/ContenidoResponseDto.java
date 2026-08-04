package com.techmind.backend.dto;

import com.techmind.backend.entity.Contenido;
import com.techmind.backend.entity.Keyword;

import java.util.Collections;
import java.util.List;

public record ContenidoResponseDto(
        Long id,
        String title,
        String text,
        String category,
        Double confidence,
        List<String> keywords
) {

    // Metodo estático para mapear de Entidad Contenido a ContenidoResponseDto
    public static ContenidoResponseDto deEntidad(Contenido contenido) {
        String category = null;
        Double confidence = null;
        List<String> keywords = Collections.emptyList();

        if (contenido.getPrediccion() != null) {
            category = contenido.getPrediccion().getCategory();
            confidence = contenido.getPrediccion().getConfidence();

            if (contenido.getPrediccion().getKeywords() != null) {
                keywords = contenido.getPrediccion().getKeywords().stream()
                        .map(Keyword::getKeyword)
                        .toList();
            }
        }

        return new ContenidoResponseDto(
                contenido.getId(),
                contenido.getTitle(),
                contenido.getText(),
                category,
                confidence,
                keywords
        );
    }
}
