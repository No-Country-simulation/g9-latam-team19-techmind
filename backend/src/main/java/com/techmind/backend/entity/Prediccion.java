package com.techmind.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "prediccion")
@SQLDelete(sql = "UPDATE prediccion SET activo = false WHERE id = ?")
@SQLRestriction("activo = true")
public class Prediccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Double confidence;

    @Column(nullable = false)
    private Boolean activo = true;

    @OneToOne
    @JoinColumn(name = "contenido_id", nullable = false)
    private Contenido contenido;

    // Se inicializa con ArrayList para evitar NullPointerException
    @OneToMany(mappedBy = "prediccion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Keyword> keywords = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "prediccion_recomendacion",
            joinColumns = @JoinColumn(name = "prediccion_id"),
            inverseJoinColumns = @JoinColumn(name = "recomendacion_id")
    )
    private List<Recomendacion> recomendaciones = new ArrayList<>();

    public Prediccion() {
    }

    public Prediccion(String category, Double confidence) {
        this.category = category;
        this.confidence = confidence;
    }
    // Se agrega metodo helper crucial para vincular la relación en ambos sentidos
    public void addKeyword(Keyword keyword) {
        keywords.add(keyword);
        keyword.setPrediccion(this);
    }

    public void addRecomendacion(Recomendacion recomendacion) {
        recomendaciones.add(recomendacion);
        //recomendacion.setPrediccion(this);
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
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

    public List<Recomendacion> getRecomendaciones() {
        return recomendaciones;
    }

    public void setRecomendaciones(List<Recomendacion> recomendaciones) {
        this.recomendaciones = recomendaciones;
    }
}