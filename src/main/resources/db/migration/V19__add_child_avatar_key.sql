-- =====================================================================
-- V19__add_child_avatar_key.sql
--
-- Avatar de una galería fija para perfiles de niños (no una foto subida):
-- solo guarda una clave (p.ej. "avatar1") que el frontend resuelve contra
-- sus propias imágenes ya incluidas en la app. Al no ser contenido subido
-- por el usuario, no necesita pasar por moderación.
-- =====================================================================

ALTER TABLE children ADD COLUMN avatar_key VARCHAR(50);
