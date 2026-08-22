package com.techmind.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "recomendacion")
@SQLDelete(sql = "UPDATE recomendacion SET activo = false WHERE id = ?")
@SQLRestriction("activo = true")
public class Recomendacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false)
    private Long externalId;

    @Column(nullable = false)
    private String title;

    @Column(name = "category_recs", nullable = false)
    private String categoryRecs;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String level;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false)
    private Boolean activo = true;

    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "prediccion_id", nullable = false)
    //private Prediccion prediccion;

    public Recomendacion() {
    }

    public Recomendacion(Long externalId,
                         String title,
                         String categoryRecs,
                         String type,
                         String level,
                         String language,
                         String url) {
        this.externalId = externalId;
        this.title = title;
        this.categoryRecs = categoryRecs;
        this.type = type;
        this.level = level;
        this.language = language;
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategoryRecs() {
        return categoryRecs;
    }

    public void setCategoryRecs(String categoryRecs) {
        this.categoryRecs = categoryRecs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    /*public Prediccion getPrediccion() {
        return prediccion;
    }*/

    /*public void setPrediccion(Prediccion prediccion) {
        this.prediccion = prediccion;
    }*/
}
