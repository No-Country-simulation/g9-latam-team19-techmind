package com.techmind.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "DTO de respuesta devuelto por el módulo de Inteligencia Artificial / Data Science")
public record DataScienceResponseDto(

        @Schema(
                description = "Categoría predicha para el contenido",
                example = "Desarrollo Web"
        )
        String category,

        @Schema(
                description = "Nivel de confianza o puntuación de la predicción obtenida",
                example = "0.95"
        )
        Double confidence,

        @Schema(
                description = "Lista de palabras clave relevantes identificadas en el texto",
                example = "[\"CSS\", \"Flexbox\", \"HTML\"]"
        )
        List<String> keywords
) {
}
