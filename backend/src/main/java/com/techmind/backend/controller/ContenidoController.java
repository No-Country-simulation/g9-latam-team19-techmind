package com.techmind.backend.controller;

import com.techmind.backend.dto.ContenidoDTO;
import com.techmind.backend.dto.PrediccionDTO;
import com.techmind.backend.service.ClasificadorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/contenido")
public class ContenidoController {

    private final ClasificadorService clasificadorService;

    public ContenidoController(ClasificadorService clasificadorService) {
        this.clasificadorService = clasificadorService;
    }

    @PostMapping
    public ResponseEntity<PrediccionDTO> procesarContenido(@Valid @RequestBody ContenidoDTO request) {
        PrediccionDTO resultado = clasificadorService.obtenerClasificacion(request);
        return ResponseEntity.ok(resultado);
    }
}
