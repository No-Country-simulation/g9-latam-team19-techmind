-- Eliminar la clave foránea anterior de la tabla recomendacion
ALTER TABLE recomendacion DROP FOREIGN KEY FK_recomendacion_prediccion;
ALTER TABLE recomendacion DROP COLUMN prediccion_id;

-- Crear la tabla intermedia para la relación ManyToMany
CREATE TABLE prediccion_recomendacion (
    prediccion_id BIGINT NOT NULL,
    recomendacion_id BIGINT NOT NULL,
    PRIMARY KEY (prediccion_id, recomendacion_id),
    CONSTRAINT fk_prediccion_rec_prediccion FOREIGN KEY (prediccion_id) REFERENCES prediccion(id),
    CONSTRAINT fk_prediccion_rec_recomendacion FOREIGN KEY (recomendacion_id) REFERENCES recomendacion(id)
);

-- 3. Garantizar la unicidad del id externo de Data Science
ALTER TABLE recomendacion ADD CONSTRAINT unique_external_id UNIQUE (external_id);