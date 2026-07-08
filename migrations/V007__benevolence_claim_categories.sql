-- ============================================================
-- V007 — Benevolence claim categories
-- Run on Railway before deploying
-- ============================================================

CREATE TABLE IF NOT EXISTS benevolence_claim_categories (
    id                  UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    name                VARCHAR(100) NOT NULL UNIQUE,
    description         VARCHAR(500),
    event_date_label    VARCHAR(100) NOT NULL DEFAULT 'Event Date',
    event_person_label  VARCHAR(100) NOT NULL DEFAULT 'Person Name',
    requires_documents  BOOLEAN     NOT NULL DEFAULT FALSE,
    active              BOOLEAN     NOT NULL DEFAULT TRUE,
    sort_order          INTEGER     NOT NULL DEFAULT 0,
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP   NOT NULL DEFAULT NOW(),
    created_by          UUID,
    updated_by          UUID,
    version             BIGINT      NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_bcc_active     ON benevolence_claim_categories(active);
CREATE INDEX IF NOT EXISTS idx_bcc_sort_order ON benevolence_claim_categories(sort_order);

-- Seed default categories
INSERT INTO benevolence_claim_categories (name, description, event_date_label, event_person_label, requires_documents, sort_order)
VALUES
  ('Death/Bereavement',       'Support for members who have lost a close family member.',     'Date of Death',      'Deceased''s Full Name',    TRUE,  1),
  ('Sickness/Medical',        'Support for members or direct family facing medical hardship.','Date of Diagnosis',  'Patient''s Name',           TRUE,  2),
  ('Wedding/Celebration',     'Support for members celebrating a wedding milestone.',         'Wedding Date',       'Couple''s Names',           FALSE, 3),
  ('Graduation',              'Support for members or children celebrating graduation.',      'Graduation Date',    'Graduate''s Name',          FALSE, 4),
  ('Birthday',                'Special recognition support for milestone birthdays.',         'Birthday Date',      'Person''s Name',            FALSE, 5),
  ('Achievement/Award',       'Support for notable achievements or awards.',                  'Achievement Date',   'Achiever''s Name',          FALSE, 6),
  ('Anniversary',             'Support for significant personal or community anniversaries.', 'Anniversary Date',   'Person/Couple Name',        FALSE, 7),
  ('Crisis/Emergency',        'Emergency support for unforeseen hardship or disaster.',       'Date of Event',      'Affected Person''s Name',   TRUE,  8)
ON CONFLICT (name) DO NOTHING;

-- Add category FK to benevolence_claims
ALTER TABLE benevolence_claims
    ADD COLUMN IF NOT EXISTS category_id UUID,
    ADD CONSTRAINT fk_claim_category FOREIGN KEY (category_id)
        REFERENCES benevolence_claim_categories(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_ben_claim_category ON benevolence_claims(category_id);
