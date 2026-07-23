package com.techmind.backend.controller;

import com.techmind.backend.dto.ContenidoRequest;
import com.techmind.backend.dto.PrediccionResponse;
import com.techmind.backend.service.ClasificadorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contenido")
public class ContenidoController {

    private final ClasificadorService clasificadorService;

    public ContenidoController(ClasificadorService clasificadorService) {
        this.clasificadorService = clasificadorService;
    }

    @PostMapping
    public ResponseEntity<PrediccionResponse> procesarContenido(@Valid @RequestBody ContenidoRequest request) {
        PrediccionResponse resultado = clasificadorService.obtenerClasificacion(request);
        return ResponseEntity.ok(resultado);
    }
}
