-- ============================================================
-- V008 — Leadership officials (admin-managed public page)
-- ============================================================

CREATE TABLE IF NOT EXISTS leadership_officials (
    id                   UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    name                 VARCHAR(150) NOT NULL,
    role                 VARCHAR(150) NOT NULL,
    team                 VARCHAR(20)  NOT NULL CHECK (team IN ('EXECUTIVE','HOSPITALITY','COMPLIANCE')),
    bio                  TEXT,
    image_url            VARCHAR(500),
    cloudinary_public_id VARCHAR(300),
    active               BOOLEAN      NOT NULL DEFAULT TRUE,
    sort_order           INTEGER      NOT NULL DEFAULT 0,
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by           VARCHAR(150),
    updated_by           VARCHAR(150),
    version              BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_lo_team       ON leadership_officials(team);
CREATE INDEX IF NOT EXISTS idx_lo_active     ON leadership_officials(active);
CREATE INDEX IF NOT EXISTS idx_lo_sort_order ON leadership_officials(sort_order);

-- Seed current hardcoded leaders so the page is not blank before admin re-enters them
INSERT INTO leadership_officials (name, role, team, bio, sort_order) VALUES
  ('Brown Mulamula',  'Executive Chairman',      'EXECUTIVE',  'Leads Ushirika Welfare DFW, chairs all executive meetings and represents the organization in the wider Luhya community.', 1),
  ('Edgar Lumbasio',  'Executive Vice Chairman', 'EXECUTIVE',  'Supports the Executive Chairman, oversees member relations and steps in to lead when the chairman is unavailable.', 2),
  ('Eugene Vida',     'Executive Treasurer',     'EXECUTIVE',  'Stewards the welfare fund — managing collections, disbursements, and all financial reporting to the membership.', 3),
  ('Joe Wamoto',      'Executive Chief Whip',    'EXECUTIVE',  'Maintains order and discipline within the organization, ensuring members uphold the constitution and bylaws.', 4),
  ('Sylvia Tirop',    'Executive Secretary',     'EXECUTIVE',  'Keeps official records, prepares meeting minutes, and manages all organizational correspondence.', 5),
  ('Martine Ndubi',   'Executive Vice Secretary','EXECUTIVE',  'Assists the Executive Secretary with records management and takes over secretarial duties when needed.', 6),
  ('Evelyn Aduo',     'Benevolence Coordinator', 'EXECUTIVE',  'Coordinates the Benevolence Program — ensuring timely support for members during bereavement, sickness, and family crises.', 7),
  ('Grace Kanyanga',  'Head — Hospitality Team', 'HOSPITALITY','Leads the hospitality committee and ensures every Ushirika gathering is warm, organized, and memorable.', 1),
  ('Josephine Kavesh','Member — Hospitality Team','HOSPITALITY','Assists with event coordination and hospitality support across all community gatherings.', 2),
  ('Carol Weche',     'Member — Hospitality Team','HOSPITALITY','Contributes to catering, venue setup, and ensuring members feel welcomed at all events.', 3),
  ('Ken Masika',      'Member — Hospitality Team','HOSPITALITY','Supports logistics and on-the-day setup for all Ushirika community events.', 4)
ON CONFLICT DO NOTHING;
