package com.techmind.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.techmind.backend.entity.Recomendacion;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de una recomendación disponible para el usuario")
public record RecomendacionResponseDto(

        @Schema(
                description = "Identificador interno de la recomendación",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Título del recurso recomendado",
                example = "MySQL Reference Manual"
        )
        String title,

        @JsonProperty("category_recs")
        @Schema(
                description = "Categoría a la que pertenece el recurso recomendado",
                example = "Bases de datos"
        )
        String categoryRecs,

        @Schema(
                description = "Tipo de recurso recomendado",
                example = "Documentación"
        )
        String type,

        @Schema(
                description = "Nivel de dificultad del recurso",
                example = "Básico"
        )
        String level,

        @Schema(
                description = "Idioma del recurso recomendado",
                example = "en"
        )
        String language,

        @Schema(
                description = "URL del recurso recomendado",
                example = "https://dev.mysql.com/doc/refman/8.4/en/"
        )
        String url

) {

    public static RecomendacionResponseDto deEntidad(
            Recomendacion recomendacion
    ) {
        return new RecomendacionResponseDto(
                recomendacion.getId(),
                recomendacion.getTitle(),
                recomendacion.getCategoryRecs(),
                recomendacion.getType(),
                recomendacion.getLevel(),
                recomendacion.getLanguage(),
                recomendacion.getUrl()
        );
    }
}
