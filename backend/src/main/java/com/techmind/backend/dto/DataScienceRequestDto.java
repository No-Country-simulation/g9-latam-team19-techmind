package com.techmind.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO de solicitud enviado al módulo de análisis de Data Science")
public record DataScienceRequestDto(
        @Schema(
                description = "Título del contenido a procesar",
                example = "HTML y CSS: Clases, Posicionamiento y Flexbox"
        )
        @NotBlank(message = "El título no puede estar vacío")
        @Size(max = 100, message = "El título no puede superar los 100 caracteres.")
        String title,

        @Schema(
                description = "Cuerpo principal o texto del artículo/contenido a analizar",
                example = "Resumen del curso. Aprende qué son las clases CSS, cómo funcionan las reglas de especificidad y el diseño flexible con Flexbox."
        )
        @NotBlank(message = "El texto no puede estar vacío")
        @Size(max = 5000, message = "El texto no puede superar los 5000 caracteres.")
        String text
) {
}
