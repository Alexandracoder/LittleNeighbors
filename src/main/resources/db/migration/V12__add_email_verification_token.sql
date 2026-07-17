-- =====================================================================
-- V12__add_email_verification_token.sql
--
-- BUG DE SEGURIDAD: el registro nunca generaba ni enviaba un token de
-- verificación de email. Cualquiera podía registrarse con un email que
-- no controla y usar la app con normalidad, porque además el login no
-- comprobaba el estado de verificación en absoluto.
-- =====================================================================

ALTER TABLE users
    ADD COLUMN email_verification_token VARCHAR(255),
    ADD COLUMN email_verification_expires TIMESTAMP;

CREATE INDEX idx_users_email_verification_token ON users(email_verification_token);
