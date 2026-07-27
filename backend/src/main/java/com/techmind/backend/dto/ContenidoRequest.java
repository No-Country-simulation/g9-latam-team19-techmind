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

    public String getTitle() { return title;}
    public void setTitle(String title) { this.title = title; }

    public String getText() { return text; }
    public  void setText(String text) { this.text = text; }
}
