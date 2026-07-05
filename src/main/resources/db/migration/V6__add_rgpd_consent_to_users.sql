-- RGPD: registro del consentimiento en el alta de usuario (Art. 7 RGPD),
-- igual que ya se hace para pilot_leads en V5.

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS consent_given          BOOLEAN     NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS consent_at             TIMESTAMP,
    ADD COLUMN IF NOT EXISTS privacy_policy_version VARCHAR(20);

-- Usuarios ya existentes (admin de seed, cuentas de prueba, altas previas a
-- este cambio): se marcan como consentidos retroactivamente con la versión
-- inicial, igual que se hizo con los pilot_leads en V5.
UPDATE users
SET consent_given = TRUE,
    consent_at = created_at,
    privacy_policy_version = '1.0'
WHERE consent_at IS NULL;

COMMENT ON COLUMN users.consent_given IS 'RGPD Art.7: consentimiento explícito del usuario en el registro';
COMMENT ON COLUMN users.consent_at IS 'RGPD Art.7: timestamp exacto del consentimiento';
COMMENT ON COLUMN users.privacy_policy_version IS 'Versión de la política de privacidad aceptada en el registro';
