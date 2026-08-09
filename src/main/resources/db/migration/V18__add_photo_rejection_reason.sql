-- =====================================================================
-- V18__add_photo_rejection_reason.sql
--
-- El rechazo de foto de familia no guardaba ningún motivo (a diferencia
-- del rechazo de verificación de identidad, que sí lo tenía desde el
-- principio). Así la familia sabe qué corregir antes de subir otra.
-- =====================================================================

ALTER TABLE families ADD COLUMN photo_rejection_reason VARCHAR(255);
