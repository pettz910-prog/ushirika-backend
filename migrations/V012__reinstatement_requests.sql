CREATE TABLE reinstatement_requests (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id),
    reason          TEXT NOT NULL,
    status          VARCHAR(10) NOT NULL DEFAULT 'PENDING'
                        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    admin_notes     TEXT,
    reviewed_by     UUID REFERENCES users(id),
    reviewed_at     TIMESTAMP,
    version         BIGINT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      UUID,
    updated_by      UUID
);

CREATE INDEX idx_reinstate_user        ON reinstatement_requests(user_id);
CREATE INDEX idx_reinstate_status      ON reinstatement_requests(status);
CREATE INDEX idx_reinstate_user_status ON reinstatement_requests(user_id, status);
CREATE INDEX idx_reinstate_created_at  ON reinstatement_requests(created_at);
