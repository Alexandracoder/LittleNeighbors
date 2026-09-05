-- =====================================================================
-- V22__cascade_delete_messages_on_user_delete.sql
--
-- Igual que V21 con events.creator_family_id, esta FK tampoco tenía
-- ON DELETE CASCADE ni SET NULL (por defecto, NO ACTION). Hacía falta
-- para poder implementar el borrado de cuenta completo
-- (DELETE /api/auth/me): si el usuario había enviado o recibido algún
-- mensaje, borrar su UserEntity fallaría con una violación de FK igual
-- que pasaba antes con los eventos.
--
-- A diferencia de events (donde SÍ tiene sentido borrar en cascada el
-- evento entero), aquí se usa ON DELETE SET NULL en vez de CASCADE:
-- al eliminar la cuenta, solo desaparece la referencia a quien se ha
-- borrado (su lado de la conversación), pero el mensaje en sí y la
-- copia de la otra persona se conservan. sender_id/receiver_id pasan
-- a permitir NULL para que esto funcione.
-- =====================================================================

ALTER TABLE messages
    ALTER COLUMN sender_id DROP NOT NULL;

ALTER TABLE messages
    ALTER COLUMN receiver_id DROP NOT NULL;

ALTER TABLE messages
    DROP CONSTRAINT fk_messages_sender;

ALTER TABLE messages
    ADD CONSTRAINT fk_messages_sender
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE messages
    DROP CONSTRAINT fk_messages_receiver;

ALTER TABLE messages
    ADD CONSTRAINT fk_messages_receiver
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE SET NULL;
