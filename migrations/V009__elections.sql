-- ============================================================
-- V009 — Election System (secret ballot architecture)
-- ============================================================

-- Main election record
CREATE TABLE IF NOT EXISTS elections (
    id                          UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    title                       VARCHAR(200) NOT NULL,
    year                        INTEGER      NOT NULL,
    nominations_start           DATE,
    nominations_end             DATE,
    voting_start                TIMESTAMP,
    voting_end                  TIMESTAMP,
    status                      VARCHAR(30)  NOT NULL DEFAULT 'DRAFT'
        CHECK (status IN ('DRAFT','NOMINATIONS_OPEN','NOMINATIONS_CLOSED','VOTING_OPEN','VOTING_CLOSED','RESULTS_PUBLISHED','COMPLETED','CANCELLED')),
    video_url                   VARCHAR(500),
    cloudinary_video_public_id  VARCHAR(300),
    notes                       TEXT,
    results_declared_at         TIMESTAMP,
    completed_at                TIMESTAMP,
    created_at                  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by                  VARCHAR(150),
    updated_by                  VARCHAR(150),
    version                     BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_elec_status ON elections(status);
CREATE INDEX IF NOT EXISTS idx_elec_year   ON elections(year);

-- Seats within an election
CREATE TABLE IF NOT EXISTS election_seats (
    id           UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    election_id  UUID         NOT NULL REFERENCES elections(id) ON DELETE CASCADE,
    title        VARCHAR(150) NOT NULL,
    description  TEXT,
    max_winners  INTEGER      NOT NULL DEFAULT 1,
    sort_order   INTEGER      NOT NULL DEFAULT 0,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by   VARCHAR(150),
    updated_by   VARCHAR(150),
    version      BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT fk_eseat_election FOREIGN KEY (election_id) REFERENCES elections(id)
);

CREATE INDEX IF NOT EXISTS idx_eseat_election ON election_seats(election_id);
CREATE INDEX IF NOT EXISTS idx_eseat_sort     ON election_seats(sort_order);

-- Candidacies (who is running for which seat)
CREATE TABLE IF NOT EXISTS election_candidacies (
    id                UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    election_id       UUID         NOT NULL REFERENCES elections(id) ON DELETE CASCADE,
    seat_id           UUID         NOT NULL REFERENCES election_seats(id) ON DELETE CASCADE,
    user_id           UUID         NOT NULL REFERENCES users(id),
    member_name       VARCHAR(200) NOT NULL,
    member_id         VARCHAR(20),
    photo_url         VARCHAR(500),
    statement         TEXT,
    status            VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING','APPROVED','REJECTED','WITHDRAWN')),
    rejection_reason  TEXT,
    reviewed_by       VARCHAR(150),
    reviewed_at       TIMESTAMP,
    withdrawn_at      TIMESTAMP,
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by        VARCHAR(150),
    updated_by        VARCHAR(150),
    version           BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT uk_candidacy_seat_user UNIQUE (seat_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_ecan_election ON election_candidacies(election_id);
CREATE INDEX IF NOT EXISTS idx_ecan_seat     ON election_candidacies(seat_id);
CREATE INDEX IF NOT EXISTS idx_ecan_user     ON election_candidacies(user_id);
CREATE INDEX IF NOT EXISTS idx_ecan_status   ON election_candidacies(status);

-- Vote receipts — proves member voted for a seat, NO candidate reference (secret ballot)
CREATE TABLE IF NOT EXISTS election_vote_receipts (
    id           UUID      NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    election_id  UUID      NOT NULL REFERENCES elections(id) ON DELETE CASCADE,
    seat_id      UUID      NOT NULL REFERENCES election_seats(id) ON DELETE CASCADE,
    voter_id     UUID      NOT NULL REFERENCES users(id),
    voted_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_receipt_election_seat_voter UNIQUE (election_id, seat_id, voter_id)
);

CREATE INDEX IF NOT EXISTS idx_evr_election ON election_vote_receipts(election_id);
CREATE INDEX IF NOT EXISTS idx_evr_seat     ON election_vote_receipts(seat_id);
CREATE INDEX IF NOT EXISTS idx_evr_voter    ON election_vote_receipts(voter_id);

-- Vote tallies — running count per candidate, NO voter reference (secret ballot)
CREATE TABLE IF NOT EXISTS election_vote_tallies (
    id            UUID   NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    candidacy_id  UUID   NOT NULL REFERENCES election_candidacies(id) ON DELETE CASCADE UNIQUE,
    vote_count    BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_evt_candidacy FOREIGN KEY (candidacy_id) REFERENCES election_candidacies(id)
);

CREATE INDEX IF NOT EXISTS idx_evt_candidacy ON election_vote_tallies(candidacy_id);

-- Official declared results (computed after voting closes)
CREATE TABLE IF NOT EXISTS election_results (
    id            UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    election_id   UUID         NOT NULL REFERENCES elections(id) ON DELETE CASCADE,
    seat_id       UUID         NOT NULL REFERENCES election_seats(id),
    candidacy_id  UUID         NOT NULL REFERENCES election_candidacies(id),
    member_name   VARCHAR(200) NOT NULL,
    member_id     VARCHAR(20),
    seat_title    VARCHAR(150) NOT NULL,
    vote_count    BIGINT       NOT NULL DEFAULT 0,
    rank          INTEGER      NOT NULL DEFAULT 1,
    is_winner     BOOLEAN      NOT NULL DEFAULT FALSE,
    tie_broken    BOOLEAN      NOT NULL DEFAULT FALSE,
    notes         VARCHAR(500),
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(150),
    updated_by    VARCHAR(150),
    version       BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_eres_election ON election_results(election_id);
CREATE INDEX IF NOT EXISTS idx_eres_seat     ON election_results(seat_id);
CREATE INDEX IF NOT EXISTS idx_eres_winner   ON election_results(is_winner);
