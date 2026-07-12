-- Sistema de reportar contenido y bloquear familias.
--
-- blocked_families: bloqueo INMEDIATO y autoservicio entre dos familias
-- (no requiere admin). Una vez bloqueada, la familia bloqueada desaparece
-- del Explorar de quien bloquea, no puede iniciar nuevos matches con ella,
-- y no se le entregan mensajes nuevos en chats ya existentes.
--
-- reports: escalado a un administrador humano. Cualquier familia puede
-- reportar un mensaje, un perfil o un evento; el admin revisa y decide si
-- desestima el reporte o bloquea la cuenta a nivel de plataforma
-- (reutilizando el flujo de verificación que ya existe en ModerationService).

CREATE TABLE blocked_families (
    id                 BIGSERIAL PRIMARY KEY,
    blocker_family_id  BIGINT NOT NULL REFERENCES families(id) ON DELETE CASCADE,
    blocked_family_id  BIGINT NOT NULL REFERENCES families(id) ON DELETE CASCADE,
    created_at         TIMESTAMP NOT NULL DEFAULT now(),
    updated_at         TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_blocked_family_pair UNIQUE (blocker_family_id, blocked_family_id),
    CONSTRAINT chk_no_self_block CHECK (blocker_family_id <> blocked_family_id)
);

CREATE INDEX idx_blocked_families_blocker ON blocked_families(blocker_family_id);
CREATE INDEX idx_blocked_families_blocked ON blocked_families(blocked_family_id);

CREATE TABLE reports (
    id                  BIGSERIAL PRIMARY KEY,
    reporter_user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reported_user_id    BIGINT REFERENCES users(id) ON DELETE SET NULL,
    reported_family_id  BIGINT REFERENCES families(id) ON DELETE SET NULL,
    report_type         VARCHAR(20) NOT NULL,
    related_id          BIGINT,
    reason              VARCHAR(50) NOT NULL,
    description         VARCHAR(1000),
    content_snapshot    VARCHAR(2000),
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP NOT NULL DEFAULT now(),
    resolved_at         TIMESTAMP,
    resolved_by_admin_id BIGINT REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_reports_status ON reports(status);
CREATE INDEX idx_reports_reported_user ON reports(reported_user_id);
