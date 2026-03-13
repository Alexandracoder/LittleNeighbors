-- 1. Primero, eliminamos los datos huérfanos que no tienen barrio asignado.
-- Como tu app es de "matching puro", una familia sin barrio no es matcheable.
DELETE FROM child_interests
WHERE child_id IN (SELECT id FROM children WHERE family_id IN (SELECT id FROM families WHERE neighborhood_id IS NULL));

DELETE FROM children
WHERE family_id IN (SELECT id FROM families WHERE neighborhood_id IS NULL);

DELETE FROM families
WHERE neighborhood_id IS NULL;

-- 2. Ahora que no hay registros nulos, forzamos la integridad.
-- Esto asegura que, en el futuro, NUNCA se pueda crear una familia sin barrio.
ALTER TABLE families MODIFY neighborhood_id BIGINT NOT NULL;

-- 3. (Opcional) Aseguramos la relación en la tabla hijos
ALTER TABLE children MODIFY family_id BIGINT NOT NULL;