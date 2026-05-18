-- Idempotente: la tabla pudo crearse antes vía Hibernate (ddl-auto)
CREATE TABLE IF NOT EXISTS imagen_salon (
    id           BIGSERIAL PRIMARY KEY,
    salon_id     BIGINT       NOT NULL REFERENCES salon(id) ON DELETE CASCADE,
    url          VARCHAR(500),
    descripcion  VARCHAR(255),
    orden        INT          NOT NULL DEFAULT 0,
    es_principal BOOLEAN      NOT NULL DEFAULT FALSE,
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    creado_en    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_imagen_salon_salon ON imagen_salon(salon_id, orden);
