package com.techmind.backend.controller;

import com.techmind.backend.dto.ContenidoResponseDto;
import com.techmind.backend.dto.DataScienceRequestDto;
import com.techmind.backend.dto.DataScienceResponseDto;
import com.techmind.backend.service.ContenidoService;
import com.techmind.backend.service.PythonApiClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contenido")
@Validated
@Tag(name = "Procesamiento de Contenido", description = "Endpoints para la gestión, análisis de IA y persistencia de contenidos")
public class ContenidoController {

    private final PythonApiClient pythonApiClient;
    private final ContenidoService contenidoService;

    public ContenidoController(PythonApiClient pythonApiClient, ContenidoService contenidoService) {
        this.pythonApiClient = pythonApiClient;
        this.contenidoService = contenidoService;
    }

    @PostMapping("/procesar")
    @Operation(
            summary = "Procesar y guardar contenido",
            description = "Envía el texto al microservicio de Python (IA) para obtener la predicción y persiste la información en la base de datos MySQL."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contenido procesado y guardado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o faltantes"),
            @ApiResponse(responseCode = "500", description = "Error al comunicar con la API de Python o al guardar en BD")
    })
    public ResponseEntity<DataScienceResponseDto> procesarYGuardar(@Valid @RequestBody DataScienceRequestDto requestDto) {
        // Consumir la api real
        DataScienceResponseDto responseDto = pythonApiClient.obtenerPrediccion(requestDto);
        // Persistir en la BD MySQL
        contenidoService.guardarContenidoYPrediccion(requestDto, responseDto);
        // Responder al cliente
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // GET /api/contenido -> Obtiene la lista completa
    @GetMapping
    @Operation(summary = "Listar todos los contenidos", description = "Obtiene la lista de todos los contenidos registrados y activos.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito")
    public ResponseEntity<List<ContenidoResponseDto>> listarTodos() {
        List<ContenidoResponseDto> lista = contenidoService.obtenerTodos();
        return ResponseEntity.ok(lista);
    }

    // GET /api/contenido/{id} -> Obtiene un registro por su ID
    @GetMapping("/{id}")
    @Operation(summary = "Obtener contenido por ID", description = "Busca un registro específico en la base de datos por su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado"),
            @ApiResponse(responseCode = "404", description = "Contenido no encontrado")
    })
    public ResponseEntity<ContenidoResponseDto> obtenerPorId(
            @PathVariable @NotNull @Min(value = 1, message = "El ID debe ser mayor a 0") Long id) {
        return ResponseEntity.ok(contenidoService.obtenerPorId(id));
    }

    // DELETE /api/contenido/{id} -> Oculta el registro cambiando activo a false
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar contenido (borrado lógico)", description = "Oculta el registro en el sistema cambiando su estado activo a false.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contenido desactivado correctamente"),
            @ApiResponse(responseCode = "404", description = "ID no encontrado")
    })
    public ResponseEntity<Void> eliminarContenido(
            @PathVariable @NotNull @Min(value = 1, message = "El ID debe ser mayor a 0") Long id) {
        contenidoService.eliminarContenido(id);
        return ResponseEntity.noContent().build(); // Retorna un HTTP 204 No Content
    }

    // PATCH /api/contenido/{id}/restaurar -> Reactiva un registro borrado lógicamente
    @PatchMapping("/{id}/restaurar")
    @Operation(summary = "Restaurar contenido", description = "Vuelve a activar un registro previamente borrado de forma lógica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contenido restaurado exitosamente"),
            @ApiResponse(responseCode = "404", description = "ID no encontrado")
    })
    public ResponseEntity<Void> restaurarContenido(
            @PathVariable @NotNull @Min(value = 1, message = "El ID debe ser mayor a 0") Long id) {
        contenidoService.restaurarContenido(id);
        return ResponseEntity.ok().build(); // Retorna un HTTP 200 OK
    }
}
