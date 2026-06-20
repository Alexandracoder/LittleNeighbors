-- 1. Primero, eliminamos los datos huérfanos que dependen de familias sin barrio.
-- Borramos los intereses de los niños de familias sin barrio
DELETE FROM child_interests
WHERE child_id IN (
    SELECT id FROM children WHERE family_id IN (
        SELECT id FROM families WHERE neighborhood_id IS NULL
    )
);

-- Borramos los niños de familias sin barrio
DELETE FROM children
WHERE family_id IN (SELECT id FROM families WHERE neighborhood_id IS NULL);

-- Finalmente, borramos las familias huérfanas
DELETE FROM families
WHERE neighborhood_id IS NULL;

-- 2. Eliminamos la Foreign Key actual que permite el SET NULL
ALTER TABLE families DROP CONSTRAINT fk_families_neighborhood;

-- 3. Modificamos la columna a NOT NULL
ALTER TABLE families MODIFY neighborhood_id BIGINT NOT NULL;

-- 4. Recreamos la Foreign Key sin el SET NULL
-- (Ahora será una restricción estricta)
ALTER TABLE families
ADD CONSTRAINT fk_families_neighborhood
FOREIGN KEY (neighborhood_id) REFERENCES neighborhoods(id);