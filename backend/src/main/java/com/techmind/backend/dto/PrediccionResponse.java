package com.techmind.backend.dto;

import java.util.List;

public class PrediccionResponse {
    private String category;
    private Double confidence;
    private List<String> keywords;

    public PrediccionResponse() {}

    public PrediccionResponse(String category, double confidence) {
        this.category = category;
        this.confidence = confidence;
        this.keywords = keywords;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }
}
