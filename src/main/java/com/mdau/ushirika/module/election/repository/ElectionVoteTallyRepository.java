package com.mdau.ushirika.module.election.repository;

import com.mdau.ushirika.module.election.entity.ElectionVoteTally;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ElectionVoteTallyRepository extends JpaRepository<ElectionVoteTally, UUID> {

    Optional<ElectionVoteTally> findByCandidacyId(UUID candidacyId);

    List<ElectionVoteTally> findAllByCandidacySeatIdOrderByVoteCountDesc(UUID seatId);

    /** Atomic increment — avoids optimistic lock contention under concurrent votes. */
    @Modifying
    @Query("UPDATE ElectionVoteTally t SET t.voteCount = t.voteCount + 1 WHERE t.candidacy.id = :candidacyId")
    void incrementVoteCount(UUID candidacyId);
}
