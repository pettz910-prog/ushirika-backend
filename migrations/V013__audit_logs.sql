-- Audit log for all significant admin actions (reinstatement, fines, dues, etc.)
CREATE TABLE IF NOT EXISTS audit_logs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_id        UUID        NOT NULL,
    actor_name      VARCHAR(200) NOT NULL,
    actor_role      VARCHAR(50) NOT NULL,
    action          VARCHAR(100) NOT NULL,
    entity_type     VARCHAR(50),
    entity_id       UUID,
    description     TEXT,
    created_at      TIMESTAMP   NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_audit_actor       ON audit_logs (actor_id);
CREATE INDEX IF NOT EXISTS idx_audit_action      ON audit_logs (action);
CREATE INDEX IF NOT EXISTS idx_audit_entity      ON audit_logs (entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_created_at  ON audit_logs (created_at DESC);
