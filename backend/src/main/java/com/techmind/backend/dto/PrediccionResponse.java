package com.techmind.backend.dto;

public class PrediccionResponse {
    private String category;
    private double confidence;
    private List<String> keywords;

    public PrediccionResponse() {}

    public PrediccionResponse(String category, double confidence) {
        this.category = category;
        this.confidence = confidence;
        this.keywords = keywords;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }
}
