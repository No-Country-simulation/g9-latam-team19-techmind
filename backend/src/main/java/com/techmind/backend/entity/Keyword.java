package com.techmind.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "keyword")
@SQLDelete(sql = "UPDATE prediccion SET activo = false WHERE id = ?")
@SQLRestriction("activo = true")
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keyword;

    @Column(nullable = false)
    private Boolean activo = true;

    // Agregamos FetchType.LAZY para optimizar el rendimiento de las consultas
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prediccion_id", nullable = false)
    private Prediccion prediccion;

    public Keyword() {
    }

    public Keyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Boolean getActivo() {return activo;}
    public void setActivo(Boolean activo) {this.activo = activo;}

    public Prediccion getPrediccion() {
        return prediccion;
    }
    public void setPrediccion(Prediccion prediccion) {
        this.prediccion = prediccion;
    }
}