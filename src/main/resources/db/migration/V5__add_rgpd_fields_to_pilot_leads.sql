-- RGPD: añadir campos de consentimiento y anonimización a pilot_leads
-- Art. 7 RGPD: registro del consentimiento con timestamp y versión de política
-- Art. 5(1)(e): limitación del plazo de conservación mediante flag de anonimización

ALTER TABLE pilot_leads
    ADD COLUMN IF NOT EXISTS consent_given        BOOLEAN     NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS consent_at           TIMESTAMP,
    ADD COLUMN IF NOT EXISTS privacy_policy_version VARCHAR(20) NOT NULL DEFAULT '1.0',
    ADD COLUMN IF NOT EXISTS anonymized           BOOLEAN     NOT NULL DEFAULT FALSE;

-- Marcar leads existentes como consentidos (migración retroactiva del piloto)
-- y con la versión inicial de la política
UPDATE pilot_leads
SET consent_given = TRUE,
    consent_at = created_at,
    privacy_policy_version = '1.0'
WHERE consent_at IS NULL;

COMMENT ON COLUMN pilot_leads.consent_given IS 'RGPD Art.7: consentimiento explícito del usuario';
COMMENT ON COLUMN pilot_leads.consent_at IS 'RGPD Art.7: timestamp exacto del consentimiento';
COMMENT ON COLUMN pilot_leads.privacy_policy_version IS 'Versión de la política de privacidad aceptada';
COMMENT ON COLUMN pilot_leads.anonymized IS 'RGPD Art.5(1)(e): email sustituido por hash SHA-256 tras 12 meses';