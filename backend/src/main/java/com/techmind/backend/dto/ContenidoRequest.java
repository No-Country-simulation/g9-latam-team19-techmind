package com.techmind.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class ContenidoRequest {

    @NotBlank(message = "El titulo es obligatorio.")
    private String title;

    @NotBlank(message = "El texto es obligatorio.")
    private String text;

    public ContenidoRequest(String title, String text) {
        this.titulo = title;
        this.texto = text;
    }

    public String getTitulo() { return title;}
    public void setTitulo(String title) { this.title = title; }

    public String getTexto() { return text; }
    public  void setTexto(String text) { this.text = text; }
}
