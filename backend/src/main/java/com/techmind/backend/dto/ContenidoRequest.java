package com.techmind.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ContenidoRequest {

    @NotBlank(message = "El titulo es obligatorio.")
    @Size(max = 200, message = "El titulo no puede superar los 200 caracteres")
    private String title;

    @NotBlank(message = "El texto es obligatorio.")
    private String text;

    public ContenidoRequest(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getTitle() { return title;}
    public void setTitle(String title) { this.title = title; }

    public String getText() { return text; }
    public  void setText(String text) { this.text = text; }
}
