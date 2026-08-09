-- =====================================================================
-- V17__add_email_verified.sql
--
-- Separa "email confirmado" de "verificación de identidad": antes se
-- deducía que el email estaba confirmado de verification_status !=
-- 'UNVERIFIED', pero eso dejó de ser cierto en cuanto UNVERIFIED pasó a
-- cubrir también "email confirmado, documentos de identidad aún sin
-- subir" (ver AuthServiceImpl.verifyEmail / login).
--
-- Las cuentas que YA tuvieran verification_status distinto de UNVERIFIED
-- (es decir, que en algún momento pasaron por el flujo antiguo) se marcan
-- aquí como email_verified = true, para no dejarlas bloqueadas por este
-- cambio.
-- =====================================================================

ALTER TABLE users ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE users SET email_verified = TRUE WHERE verification_status <> 'UNVERIFIED';
