package com.techmind.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "contenido")
public class Contenido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

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

    public Prediccion getPrediccion() {
        return prediccion;
    }
    public void setPrediccion(Prediccion prediccion) {
        this.prediccion = prediccion;
    }
}