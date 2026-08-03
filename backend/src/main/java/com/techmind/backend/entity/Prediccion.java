package com.techmind.backend.entity;

import jakarta.persistence.*;
import java.util.List;


@Entity
@Table(name = "prediccion")
public class Prediccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Double confidence;

    @OneToOne
    @JoinColumn(name = "contenido_id", nullable = false)
    private Contenido contenido;

    @OneToMany(mappedBy = "prediccion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Keyword> keywords;

    public Prediccion() {
    }

    public Prediccion(String category, Double confidence) {
        this.category = category;
        this.confidence = confidence;
    }

    public Long getId() {
        return id;
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

    public Contenido getContenido() {
        return contenido;
    }
    public void setContenido(Contenido contenido) {
        this.contenido = contenido;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }
    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }
}