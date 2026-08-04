-- 1. Tabla de Contenido
CREATE TABLE contenido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    text TEXT NOT NULL
);

-- 2. Tabla de Predicción (Relación 1 a 1 con Contenido)
CREATE TABLE prediccion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(255) NOT NULL,
    confidence DOUBLE NOT NULL,
    contenido_id BIGINT NOT NULL,
    CONSTRAINT fk_prediccion_contenido
        FOREIGN KEY (contenido_id) REFERENCES contenido(id)
        ON DELETE CASCADE
);

-- 3. Tabla de Keyword (Relación N a 1 con Predicción)
CREATE TABLE keyword (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    keyword VARCHAR(255) NOT NULL,
    prediccion_id BIGINT NOT NULL,
    CONSTRAINT fk_keyword_prediccion
        FOREIGN KEY (prediccion_id) REFERENCES prediccion(id)
        ON DELETE CASCADE
);