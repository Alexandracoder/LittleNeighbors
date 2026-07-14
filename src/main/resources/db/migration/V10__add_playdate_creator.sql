-- =====================================================================
-- V10__add_playdate_creator.sql
--
-- BUG: nada en el modelo de datos guardaba quién propuso una quedada.
-- Consecuencia real observada: quien la creaba también podía pulsar
-- "Confirmar" sobre su propia propuesta (el endpoint /confirm no
-- comprobaba nada), así que la quedada quedaba en estado ACCEPTED antes
-- de que la otra familia hubiera tenido oportunidad de verla o decidir.
-- Guardamos quién la creó para poder exigir que sea la OTRA familia
-- quien confirme o rechace.
-- =====================================================================

ALTER TABLE playdates
    ADD COLUMN created_by_family_id BIGINT;

ALTER TABLE playdates
    ADD CONSTRAINT fk_playdate_created_by
    FOREIGN KEY (created_by_family_id) REFERENCES families(id) ON DELETE SET NULL;

-- Para las quedadas ya existentes no tenemos forma de saber con certeza
-- quién las creó; las dejamos con NULL (createdByFamily nulo) y el
-- backend las trata como "sin restricción de quién puede confirmar",
-- para no romper datos de la demo ya creados.
