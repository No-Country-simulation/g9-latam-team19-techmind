package com.techmind.backend.dto;

public class PrediccionResponse {
    private String category;
    private double confidence;

    public PrediccionResponse() {}

    public PrediccionResponse(String category, double confidence) {
        this.category = category;
        this.confidence = confidence;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
}
