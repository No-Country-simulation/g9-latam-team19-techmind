package com.techmind.backend.dto;

import com.techmind.backend.entity.Contenido;
import com.techmind.backend.entity.Keyword;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;
import java.util.List;

@Schema(description = "DTO de respuesta detallado con la información completa del contenido y su predicción")
public record ContenidoResponseDto(
        @Schema(description = "Identificador único del registro", example = "1")
        Long id,

        @Schema(description = "Título del contenido", example = "HTML y CSS: Clases, Posicionamiento y Flexbox")
        String title,

        @Schema(description = "Texto completo guardado en la base de datos", example = "Resumen del curso. Aprende qué son las clases CSS...")
        String text,

        @Schema(description = "Categoría asignada por el servicio de IA", example = "Desarrollo Web")
        String category,

        @Schema(description = "Nivel de confianza de la predicción realizada", example = "0.95")
        Double confidence,

        @Schema(description = "Lista de palabras clave asociadas al contenido", example = "[\"CSS\", \"Flexbox\", \"HTML\"]")
        List<String> keywords,

        @Schema(description = "Lista de contenidos recomendados")
        List<RecomendacionDTO> recommendations
) {

    // Metodo estático para mapear de Entidad Contenido a ContenidoResponseDto
    public static ContenidoResponseDto deEntidad(Contenido contenido) {
        String category = null;
        Double confidence = null;
        List<String> keywords = Collections.emptyList();
        List<RecomendacionDTO> recommendations = Collections.emptyList();

        if (contenido.getPrediccion() != null) {
            category = contenido.getPrediccion().getCategory();
            confidence = contenido.getPrediccion().getConfidence();

            if (contenido.getPrediccion().getKeywords() != null) {
                keywords = contenido.getPrediccion().getKeywords().stream()
                        .map(Keyword::getKeyword)
                        .toList();
            }

            // Mapeo de la lista de Recomendaciones
            if (contenido.getPrediccion().getRecomendaciones() != null) {
                recommendations = contenido.getPrediccion().getRecomendaciones().stream()
                        .filter(recom -> recom.getActivo() == null || recom.getActivo()) // Filtra solo activas
                        .map(RecomendacionDTO::deEntidad) // O el nombre del metodo mapper en tu RecomendacionDTO
                        .toList();
            }
        }

        return new ContenidoResponseDto(
                contenido.getId(),
                contenido.getTitle(),
                contenido.getText(),
                category,
                confidence,
                keywords,
                recommendations
        );
    }
}
