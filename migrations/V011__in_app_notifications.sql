-- ============================================================
-- V011 — In-App Notifications
-- ============================================================

CREATE TABLE IF NOT EXISTS in_app_notifications (
    id          UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id     UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category    VARCHAR(25) NOT NULL
        CHECK (category IN (
            'ANNOUNCEMENT','MEETING_REMINDER','ATTENDANCE_WARNING','FINE',
            'WELFARE_CLAIM','REPLENISHMENT','MGR_PAYMENT','DUES_REMINDER',
            'ELECTION','GENERAL'
        )),
    title       VARCHAR(200) NOT NULL,
    body        TEXT         NOT NULL,
    action_url  VARCHAR(500),
    read        BOOLEAN      NOT NULL DEFAULT FALSE,
    read_at     TIMESTAMP,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(150),
    updated_by  VARCHAR(150),
    version     BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_ian_user       ON in_app_notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_ian_read       ON in_app_notifications(read);
CREATE INDEX IF NOT EXISTS idx_ian_user_read  ON in_app_notifications(user_id, read);
CREATE INDEX IF NOT EXISTS idx_ian_created_at ON in_app_notifications(created_at);
