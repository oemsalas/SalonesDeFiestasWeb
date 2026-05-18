-- Alinea esquema si la tabla fue creada por Hibernate o por V2 sin V3
ALTER TABLE imagen_salon DROP COLUMN IF EXISTS url;

ALTER TABLE imagen_salon ADD COLUMN IF NOT EXISTS nombre_archivo VARCHAR(255);
ALTER TABLE imagen_salon ADD COLUMN IF NOT EXISTS content_type VARCHAR(100);
ALTER TABLE imagen_salon ADD COLUMN IF NOT EXISTS tamano_bytes BIGINT;
ALTER TABLE imagen_salon ADD COLUMN IF NOT EXISTS contenido BYTEA;

UPDATE imagen_salon SET tamano_bytes = 0 WHERE tamano_bytes IS NULL;
ALTER TABLE imagen_salon ALTER COLUMN tamano_bytes SET DEFAULT 0;
ALTER TABLE imagen_salon ALTER COLUMN tamano_bytes SET NOT NULL;
