-- =====================================================================
-- V21__cascade_delete_events_on_family_delete.sql
--
-- BUG encontrado al implementar el borrado de perfil (self-service):
-- la FK events.creator_family_id -> families(id) no tenía ON DELETE
-- CASCADE ni SET NULL (por defecto, NO ACTION). Si la familia había
-- creado algún evento/quedada, DELETE FROM families fallaba con una
-- violación de FK y el endpoint devolvía 500, en vez de completarse.
--
-- event_attendances y event_dismissals ya cascadean sobre events(id)
-- (ver V11 y V14), así que basta con arreglar esta FK para que todo
-- lo relacionado con los eventos de la familia borrada se limpie en
-- cascada correctamente.
-- =====================================================================

ALTER TABLE events
    DROP CONSTRAINT fk_events_creator_family;

ALTER TABLE events
    ADD CONSTRAINT fk_events_creator_family
    FOREIGN KEY (creator_family_id) REFERENCES families(id) ON DELETE CASCADE;
