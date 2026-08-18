package com.techmind.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.techmind.backend.entity.Recomendacion;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de un contenido recomendado por el servicio de IA")
public record RecomendacionDTO(
        @Schema(description = "Identificador del recurso recomendado en el servicio de IA", example = "23")
        Long id,

        @Schema(description = "Título del contenido recomendado", example = "Angular Routing")
        String title,

        @JsonProperty("category_recs")
        @Schema(description = "Categoría del recurso recomendado", example = "Frontend")
        String categoryRecs,

        @Schema(description = "Tipo de contenido", example = "Course")
        String type,

        @Schema(description = "Nivel de dificultad", example = "Intermediate")
        String level,

        @Schema(description = "Idioma del recurso", example = "Spanish")
        String language,

        @Schema(description = "Enlace al contenido", example = "https://example.com")
        String url
) {
    public static RecomendacionDTO deEntidad(Recomendacion recomendacion) {
        return new RecomendacionDTO(
                recomendacion.getExternalId(),
                recomendacion.getTitle(),
                recomendacion.getCategoryRecs(),
                recomendacion.getType(),
                recomendacion.getLevel(),
                recomendacion.getLanguage(),
                recomendacion.getUrl()
        );
    }
}
