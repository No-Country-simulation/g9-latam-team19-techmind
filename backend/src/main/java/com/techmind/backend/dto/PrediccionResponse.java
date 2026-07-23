package com.techmind.backend.dto;

public class PrediccionResponse {
    private String categoria;
    private double probabilidad;

    public PrediccionResponse() {}

    public PrediccionResponse(String categoria, double probabilidad) {
        this.categoria = categoria;
        this.probabilidad = probabilidad;
    }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public double getProbabilidad() { return probabilidad; }
    public void setProbabilidad(double probabilidad) { this.probabilidad = probabilidad; }
}
