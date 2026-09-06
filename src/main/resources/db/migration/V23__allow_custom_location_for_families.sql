-- =====================================================================
-- V23__allow_custom_location_for_families.sql
--
-- Luz García (usuaria real) no pudo registrarse porque vive en un pueblo
-- fuera de los barrios piloto de Valencia capital que hay sembrados en
-- `neighborhoods`, y el desplegable no daba otra opción. neighborhood_id
-- era obligatorio (NOT NULL) sin ninguna alternativa.
--
-- Se añade custom_location_name para poder guardar su localidad como
-- texto libre cuando no encaja en la lista, y neighborhood_id pasa a ser
-- opcional (la capa de servicio exige que venga uno de los dos, nunca
-- ninguno).
-- =====================================================================

ALTER TABLE families
    ALTER COLUMN neighborhood_id DROP NOT NULL;

ALTER TABLE families
    ADD COLUMN custom_location_name VARCHAR(255);
