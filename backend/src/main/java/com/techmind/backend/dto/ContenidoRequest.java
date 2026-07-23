package com.techmind.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class ContenidoRequest {

    @NotBlank(message = "El titulo es obligatorio.")
    private String titulo;

    @NotBlank(message = "El texto es obligatorio.")
    private String texto;

    public ContenidoRequest(String titulo, String texto) {
        this.titulo = titulo;
        this.texto = texto;
    }

    public String getTitulo() { return titulo;}
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTexto() { return texto; }
    public  void setTexto(String texto) { this.texto = texto; }
}
