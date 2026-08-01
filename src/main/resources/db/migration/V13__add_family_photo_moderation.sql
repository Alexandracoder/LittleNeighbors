ALTER TABLE families
    ADD COLUMN photo_moderation_status VARCHAR(20);

-- Las fotos que ya estaban publicadas antes de este cambio se dan por
-- aprobadas (no queremos ocultar de golpe fotos de familias del piloto que
-- ya llevaban tiempo visibles). A partir de ahora, cualquier foto nueva o
-- cambiada entra en PENDING y requiere aprobación de un admin antes de
-- mostrarse públicamente.
UPDATE families
SET photo_moderation_status = 'APPROVED'
WHERE profile_picture_url IS NOT NULL;
