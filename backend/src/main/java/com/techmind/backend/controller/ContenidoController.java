package com.techmind.backend.controller;

import com.techmind.backend.dto.ContenidoResponseDto;
import com.techmind.backend.dto.DataScienceRequestDto;
import com.techmind.backend.dto.DataScienceResponseDto;
import com.techmind.backend.service.ContenidoService;
import com.techmind.backend.service.PythonApiClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contenido")
public class ContenidoController {

    private final PythonApiClient pythonApiClient;
    private final ContenidoService contenidoService;

    public ContenidoController(PythonApiClient pythonApiClient, ContenidoService contenidoService) {
        this.pythonApiClient = pythonApiClient;
        this.contenidoService = contenidoService;
    }

    @PostMapping("/procesar")
    public ResponseEntity<DataScienceResponseDto> procesarYGuardar(@RequestBody DataScienceRequestDto requestDto) {
        // Consumir la api real
        DataScienceResponseDto responseDto = pythonApiClient.obtenerPrediccion(requestDto);
        // Persistir en la BD MySQL
        contenidoService.guardarContenidoYPrediccion(requestDto, responseDto);
        // Responder al cliente
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // GET /api/contenido -> Obtiene la lista completa
    @GetMapping
    public ResponseEntity<List<ContenidoResponseDto>> listarTodos() {
        List<ContenidoResponseDto> lista = contenidoService.obtenerTodos();
        return ResponseEntity.ok(lista);
    }

    // GET /api/contenido/{id} -> Obtiene un registro por su ID
    @GetMapping("/{id}")
    public ResponseEntity<ContenidoResponseDto> obtenerPorId(@PathVariable Long id) {
        return contenidoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
