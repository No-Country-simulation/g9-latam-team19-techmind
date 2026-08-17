package com.techmind.backend.dto;

import java.util.List;

public class PrediccionDTO {
    private String category;
    private Double confidence;
    private List<String> keywords;

    public PrediccionDTO() {
    }

    // Agregamos List<String> keywords a los argumentos del constructor para que reciba el listado de palabras clave correctamente.
    public PrediccionDTO(String category, Double confidence, List<String> keywords) {
        this.category = category;
        this.confidence = confidence;
        this.keywords = keywords;
    }

    
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }


    public Double getConfidence() {
        return confidence;
    }
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public List<String> getKeywords() {
        return keywords;
    }
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
