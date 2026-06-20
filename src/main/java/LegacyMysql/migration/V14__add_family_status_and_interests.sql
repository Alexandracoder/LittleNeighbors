-- V14__add_family_status_and_interests.sql

-- 1. Añadimos la columna status a la tabla de familias
-- La ponemos como NOT NULL con un valor por defecto para no romper los registros existentes
ALTER TABLE families
ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'WAITING_PROFILE';

-- 2. Creamos la tabla secundaria para los intereses (ElementCollection)
CREATE TABLE family_interests (
    family_id BIGINT NOT NULL,
    interest VARCHAR(255),
    CONSTRAINT fk_family_interests_family FOREIGN KEY (family_id) REFERENCES families(id) ON DELETE CASCADE
);

-- 3. Índice de rendimiento para búsquedas por intereses
CREATE INDEX idx_family_interests_family_id ON family_interests(family_id);