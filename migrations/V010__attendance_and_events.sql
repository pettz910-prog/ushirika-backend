-- ============================================================
-- V010 — Attendance & Events
-- ============================================================

-- ── Meetings ─────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS meetings (
    id           UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    title        VARCHAR(200) NOT NULL,
    description  VARCHAR(1000),
    meeting_date TIMESTAMP    NOT NULL,
    location     VARCHAR(300),
    type         VARCHAR(20)  NOT NULL
        CHECK (type IN ('QUARTERLY_AGM','QUARTERLY','SPECIAL')),
    status       VARCHAR(20)  NOT NULL DEFAULT 'SCHEDULED'
        CHECK (status IN ('SCHEDULED','COMPLETED','CANCELLED')),
    notes        VARCHAR(500),
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by   VARCHAR(150),
    updated_by   VARCHAR(150),
    version      BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_meetings_date   ON meetings(meeting_date);
CREATE INDEX IF NOT EXISTS idx_meetings_status ON meetings(status);
CREATE INDEX IF NOT EXISTS idx_meetings_type   ON meetings(type);

-- ── Attendance Records ────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS attendance_records (
    id             UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    meeting_id     UUID        NOT NULL REFERENCES meetings(id)  ON DELETE CASCADE,
    user_id        UUID        NOT NULL REFERENCES users(id)     ON DELETE CASCADE,
    status         VARCHAR(20) NOT NULL
        CHECK (status IN ('PRESENT','LATE','ABSENT','EXCUSED')),
    checked_in_at  TIMESTAMP,
    notes          VARCHAR(500),
    created_at     TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP   NOT NULL DEFAULT NOW(),
    created_by     VARCHAR(150),
    updated_by     VARCHAR(150),
    version        BIGINT      NOT NULL DEFAULT 0,
    CONSTRAINT uk_attendance_meeting_user UNIQUE (meeting_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_ar_user    ON attendance_records(user_id);
CREATE INDEX IF NOT EXISTS idx_ar_meeting ON attendance_records(meeting_id);
CREATE INDEX IF NOT EXISTS idx_ar_status  ON attendance_records(status);

-- ── Fines ─────────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS fines (
    id            UUID           NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id       UUID           NOT NULL REFERENCES users(id)    ON DELETE CASCADE,
    meeting_id    UUID                    REFERENCES meetings(id) ON DELETE SET NULL,
    reason        VARCHAR(500)   NOT NULL,
    amount        NUMERIC(10,2)  NOT NULL,
    due_date      DATE           NOT NULL,
    status        VARCHAR(20)    NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING','PAID','WAIVED')),
    waived_reason VARCHAR(500),
    paid_at       TIMESTAMP,
    created_at    TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP      NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(150),
    updated_by    VARCHAR(150),
    version       BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_fines_user     ON fines(user_id);
CREATE INDEX IF NOT EXISTS idx_fines_status   ON fines(status);
CREATE INDEX IF NOT EXISTS idx_fines_due_date ON fines(due_date);

-- ── Events ───────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS events (
    id                    UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    title                 VARCHAR(200) NOT NULL,
    description           TEXT,
    type                  VARCHAR(15)  NOT NULL
        CHECK (type IN ('GENERAL','MEMBERS_ONLY','FUNDRAISER','TRAINING','AGM','SOCIAL')),
    status                VARCHAR(15)  NOT NULL DEFAULT 'DRAFT'
        CHECK (status IN ('DRAFT','PUBLISHED','ONGOING','COMPLETED','CANCELLED')),
    venue                 VARCHAR(300),
    online_link           VARCHAR(500),
    start_date_time       TIMESTAMP    NOT NULL,
    end_date_time         TIMESTAMP,
    registration_deadline TIMESTAMP,
    capacity              INTEGER,
    members_only          BOOLEAN      NOT NULL DEFAULT FALSE,
    cover_image_url       VARCHAR(1000),
    tags                  JSONB        NOT NULL DEFAULT '[]',
    created_at            TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by            VARCHAR(150),
    updated_by            VARCHAR(150),
    version               BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_ev_status         ON events(status);
CREATE INDEX IF NOT EXISTS idx_ev_type           ON events(type);
CREATE INDEX IF NOT EXISTS idx_ev_start_dt       ON events(start_date_time);
CREATE INDEX IF NOT EXISTS idx_ev_members_only   ON events(members_only);
CREATE INDEX IF NOT EXISTS idx_ev_status_members ON events(status, members_only);
CREATE INDEX IF NOT EXISTS idx_ev_created_at     ON events(created_at);

-- ── Event Registrations ───────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS event_registrations (
    id                   UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    event_id             UUID         NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    -- NULL for guest registrations
    user_id              UUID                  REFERENCES users(id)  ON DELETE CASCADE,
    -- Guest-only fields (null when user_id is set)
    guest_name           VARCHAR(150),
    guest_email          VARCHAR(255),
    guest_phone          VARCHAR(30),
    is_member_reg        BOOLEAN      NOT NULL DEFAULT FALSE,
    status               VARCHAR(20)  NOT NULL DEFAULT 'REGISTERED'
        CHECK (status IN ('REGISTERED','ATTENDED','NO_SHOW','CANCELLED')),
    reference_code       VARCHAR(50)  NOT NULL UNIQUE,
    registered_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    attended_at          TIMESTAMP,
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by           VARCHAR(150),
    updated_by           VARCHAR(150),
    version              BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_er_event  ON event_registrations(event_id);
CREATE INDEX IF NOT EXISTS idx_er_user   ON event_registrations(user_id);
CREATE INDEX IF NOT EXISTS idx_er_status ON event_registrations(status);
CREATE INDEX IF NOT EXISTS idx_er_ref    ON event_registrations(reference_code);
