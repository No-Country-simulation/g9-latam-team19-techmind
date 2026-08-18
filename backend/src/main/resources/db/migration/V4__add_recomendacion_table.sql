-- Tabla de Recomendación (Relación N a 1 con Predicción)
CREATE TABLE recomendacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    external_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    category_recs VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    level VARCHAR(100) NOT NULL,
    language VARCHAR(100) NOT NULL,
    url VARCHAR(500) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    prediccion_id BIGINT NOT NULL,
    CONSTRAINT fk_recomendacion_prediccion
        FOREIGN KEY (prediccion_id) REFERENCES prediccion(id)
        ON DELETE CASCADE
);