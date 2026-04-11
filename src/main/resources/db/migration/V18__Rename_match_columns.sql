-- 1. Eliminar la restricción que bloquea el cambio
ALTER TABLE matches DROP CHECK chk_different_children;

-- 2. Renombrar las columnas
ALTER TABLE matches
    CHANGE COLUMN child_a_id child_request_id BIGINT NOT NULL,
    CHANGE COLUMN child_b_id child_target_id BIGINT NOT NULL;

-- 3. Volver a crear la restricción con los nuevos nombres
ALTER TABLE matches
    ADD CONSTRAINT chk_different_children CHECK (child_request_id <> child_target_id);