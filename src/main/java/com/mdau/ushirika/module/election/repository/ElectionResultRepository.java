package com.mdau.ushirika.module.election.repository;

import com.mdau.ushirika.module.election.entity.ElectionResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ElectionResultRepository extends JpaRepository<ElectionResult, UUID> {

    List<ElectionResult> findAllByElectionIdOrderBySeatTitleAscRankAsc(UUID electionId);

    List<ElectionResult> findAllByElectionIdAndWinnerTrueOrderBySeatTitleAsc(UUID electionId);

    void deleteAllByElectionId(UUID electionId);
}
