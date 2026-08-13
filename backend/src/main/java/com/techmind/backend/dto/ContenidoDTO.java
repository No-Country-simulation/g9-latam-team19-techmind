package com.techmind.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class ContenidoDTO {

    @NotBlank(message = "El titulo es obligatorio.")
    private String title;

    @NotBlank(message = "El texto es obligatorio.")
    private String text;

    public ContenidoDTO(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public ContenidoDTO() {
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }


    public String getText() {
        return text;
    }
    public  void setText(String text) {
        this.text = text;
    }
}
