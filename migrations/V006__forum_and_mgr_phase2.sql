-- ============================================================
-- V006 — Forum posts + MGR Phase 2 schema changes
-- Run on Railway via psql or pgAdmin before deploying
-- ============================================================

-- ── Forum posts ───────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS forum_posts (
    id              UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    member_id       UUID        NOT NULL,
    title           VARCHAR(200) NOT NULL,
    body            TEXT        NOT NULL,
    video_url       VARCHAR(500),
    status          VARCHAR(10) NOT NULL DEFAULT 'PENDING',
    admin_notes     TEXT,
    reviewed_by_id  UUID,
    reviewed_at     TIMESTAMP,
    approved_at     TIMESTAMP,
    featured        BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    created_by      UUID,
    updated_by      UUID,
    version         BIGINT      NOT NULL DEFAULT 0,
    CONSTRAINT fk_fp_member        FOREIGN KEY (member_id)       REFERENCES users(id),
    CONSTRAINT fk_fp_reviewed_by   FOREIGN KEY (reviewed_by_id)  REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_fp_status      ON forum_posts(status);
CREATE INDEX IF NOT EXISTS idx_fp_member_id   ON forum_posts(member_id);
CREATE INDEX IF NOT EXISTS idx_fp_approved_at ON forum_posts(approved_at);

CREATE TABLE IF NOT EXISTS forum_post_media (
    post_id     UUID        NOT NULL,
    url         VARCHAR(500),
    sort_order  INTEGER     NOT NULL,
    CONSTRAINT fk_fpm_post FOREIGN KEY (post_id) REFERENCES forum_posts(id) ON DELETE CASCADE
);

-- ── MGR Cycles — add new admin-configurable columns ──────────

ALTER TABLE mgr_cycles
    ADD COLUMN IF NOT EXISTS enrollment_open     BOOLEAN        NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS benefit_payout_day  INTEGER        NOT NULL DEFAULT 15,
    ADD COLUMN IF NOT EXISTS reserve_percentage  NUMERIC(5, 2)  NOT NULL DEFAULT 0;

-- ── MGR Slots — make payout fields nullable, add audit columns

-- payoutMonth may be NULL until drawn
ALTER TABLE mgr_slots
    ALTER COLUMN payout_month         DROP NOT NULL,
    ALTER COLUMN payout_order         DROP NOT NULL,
    ALTER COLUMN scheduled_payout_date DROP NOT NULL,
    ALTER COLUMN payout_amount        DROP NOT NULL;

-- Remove old unique constraint on (cycle_id, slot_number) if it exists
ALTER TABLE mgr_slots
    DROP CONSTRAINT IF EXISTS uq_mgr_slot_cycle_slot;

-- Add new status value to the enum (PostgreSQL requires explicit ALTER TYPE)
-- If the enum already has DRAWN this is a no-op, otherwise run it:
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_enum e
        JOIN pg_type t ON e.enumtypid = t.oid
        WHERE t.typname = 'slot_status' AND e.enumlabel = 'DRAWN'
    ) THEN
        ALTER TYPE slot_status ADD VALUE 'DRAWN' AFTER 'SCHEDULED';
    END IF;
END $$;

-- If status column is VARCHAR (not pg enum), no type change needed:
-- The above DO block is safe regardless.

-- Add audit/receipt columns to mgr_slots
ALTER TABLE mgr_slots
    ADD COLUMN IF NOT EXISTS drawn_at               TIMESTAMP,
    ADD COLUMN IF NOT EXISTS receipt_confirmed       BOOLEAN     NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS receipt_confirmed_at    TIMESTAMP,
    ADD COLUMN IF NOT EXISTS receipt_notes           VARCHAR(500);

CREATE INDEX IF NOT EXISTS idx_mgr_slot_payout_month ON mgr_slots(cycle_id, payout_month);

-- ── MGR Join Requests — new table ─────────────────────────────

CREATE TABLE IF NOT EXISTS mgr_join_requests (
    id              UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    cycle_id        UUID        NOT NULL,
    user_id         UUID        NOT NULL,
    status          VARCHAR(15) NOT NULL DEFAULT 'PENDING',
    member_notes    VARCHAR(500),
    admin_notes     VARCHAR(500),
    responded_by_id UUID,
    responded_at    TIMESTAMP,
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    created_by      UUID,
    updated_by      UUID,
    version         BIGINT      NOT NULL DEFAULT 0,
    CONSTRAINT uq_mgr_jr_cycle_user     UNIQUE (cycle_id, user_id),
    CONSTRAINT fk_mgr_jr_cycle          FOREIGN KEY (cycle_id)         REFERENCES mgr_cycles(id),
    CONSTRAINT fk_mgr_jr_user           FOREIGN KEY (user_id)          REFERENCES users(id),
    CONSTRAINT fk_mgr_jr_responded_by   FOREIGN KEY (responded_by_id)  REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_mgr_jr_cycle  ON mgr_join_requests(cycle_id);
CREATE INDEX IF NOT EXISTS idx_mgr_jr_user   ON mgr_join_requests(user_id);
CREATE INDEX IF NOT EXISTS idx_mgr_jr_status ON mgr_join_requests(status);
