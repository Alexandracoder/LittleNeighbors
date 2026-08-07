-- =====================================================================
-- V16__add_selfie_url.sql
--
-- Añade selfie_url junto a id_document_url (ya existente desde V1), para
-- poder implementar la verificación de identidad manual: la familia sube
-- foto del DNI/carnet + un selfie, un admin lo revisa a ojo en el panel y
-- aprueba/rechaza. Ambas URLs se borran de la base de datos en cuanto se
-- resuelve la revisión (ver ModerationServiceImpl) — solo se conserva el
-- resultado (verification_status), no los documentos.
-- =====================================================================

ALTER TABLE users ADD COLUMN selfie_url VARCHAR(255);
