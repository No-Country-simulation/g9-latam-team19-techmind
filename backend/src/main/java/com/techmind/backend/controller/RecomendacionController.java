package com.techmind.backend.controller;

import com.techmind.backend.dto.RecomendacionResponseDto;
import com.techmind.backend.service.RecomendacionService;
import com.techmind.backend.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/recomendaciones")
@Validated(ValidationGroups.SecuenciaOrdenada.class)
@Tag(
        name = "Recomendaciones",
        description = "Operaciones relacionadas con el catálogo de recomendaciones"
)
public class RecomendacionController {

    private final RecomendacionService recomendacionService;

    public RecomendacionController(RecomendacionService recomendacionService) {
        this.recomendacionService = recomendacionService;
    }

    @Operation(
            summary = "Listar recomendaciones",
            description = "Obtiene todas las recomendaciones activas disponibles"
    )
    @GetMapping
    public ResponseEntity<List<RecomendacionResponseDto>> listarTodas() {
        return ResponseEntity.ok(recomendacionService.obtenerTodas());
    }

    @Operation(
            summary = "Obtener recomendación por ID",
            description = "Obtiene una recomendación activa utilizando su identificador"
    )
    @GetMapping("/{id}")
    public ResponseEntity<RecomendacionResponseDto> obtenerPorId(
            @PathVariable @Min(value = 1, message = "El ID debe ser mayor a 0") Long id
    ) {
        return ResponseEntity.ok(recomendacionService.obtenerPorId(id));
    }

    @Operation(
            summary = "Buscar recomendaciones por idioma",
            description = "Obtiene las recomendaciones activas disponibles en un idioma específico"
    )
    @GetMapping(params = "language")
    public ResponseEntity<List<RecomendacionResponseDto>> obtenerPorLanguage(
            @RequestParam
            @NotBlank(message = "El lenguaje no puede estar vacío")
            @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ]+(\\\\s[a-zA-ZáéíóúÁÉÍÓÚñÑ]+)*$", message = "El idioma solo debe contener letras") String language
    ) {
        return ResponseEntity.ok(
                recomendacionService.obtenerPorLanguage(language)
        );
    }

    @Operation(
            summary = "Buscar recomendaciones por nivel",
            description = "Obtiene las recomendaciones activas correspondientes a un nivel de dificultad"
    )
    @GetMapping(params = "level")
    public ResponseEntity<List<RecomendacionResponseDto>> obtenerPorLevel(
            @RequestParam
            @NotBlank(message = "El nivel no puede estar vacio")
            @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ]+(\\\\s[a-zA-ZáéíóúÁÉÍÓÚñÑ]+)*$", message = "El nivel solo debe contener letras") String level
    ) {
        return ResponseEntity.ok(
                recomendacionService.obtenerPorLevel(level)
        );
    }

    @Operation(
            summary = "Buscar recomendaciones por categoría",
            description = "Obtiene las recomendaciones activas pertenecientes a una categoría específica"
    )
    @GetMapping(params = "category")
    public ResponseEntity<List<RecomendacionResponseDto>> obtenerPorCategoria(
            @RequestParam
            @NotBlank(message = "La categoria no puede estar vacia")
            @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ]+(\\\\s[a-zA-ZáéíóúÁÉÍÓÚñÑ]+)*$", message = "La categoria solo debe contener letras") String category
    ) {
        return ResponseEntity.ok(
                recomendacionService.obtenerPorCategoria(category)
        );
    }

    @Operation(
            summary = "Buscar recomendaciones por tipo",
            description = "Obtiene las recomendaciones activas correspondientes a un tipo de recurso"
    )
    @GetMapping(params = "type")
    public ResponseEntity<List<RecomendacionResponseDto>> obtenerPorTipo(
            @RequestParam
            @NotBlank(message = "El tipo no puede estar vacio")
            @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ]+(\\\\s[a-zA-ZáéíóúÁÉÍÓÚñÑ]+)*$", message = "El tipo solo debe contener letras") String type
    ) {
        return ResponseEntity.ok(
                recomendacionService.obtenerPorTipo(type)
        );
    }
}