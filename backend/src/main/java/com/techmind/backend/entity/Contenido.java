package com.techmind.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "contenido")
// Intercepta el DELETE de JPA y ejecuta un UPDATE en su lugar
@SQLDelete(sql = "UPDATE contenido SET activo = false WHERE id = ?")
// Filtra automáticamente los registros inactivos en las consultas (findAll, findById, etc.)
@SQLRestriction("activo = true")
public class Contenido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(nullable = false)
    private Boolean activo = true;

    @OneToOne(mappedBy = "contenido", cascade = CascadeType.ALL)
    private Prediccion prediccion;

    public Contenido() {
    }

    public Contenido(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public Long getId() {
        return id;
    }
    // falto agregar el setter error mio
    public void setId(Long id) {
        this.id = id;
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
    public void setText(String text) {
        this.text = text;
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