package com.mdau.ushirika.module.leadership.repository;

import com.mdau.ushirika.module.leadership.entity.LeadershipOfficial;
import com.mdau.ushirika.module.leadership.enums.LeadershipTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LeadershipOfficialRepository extends JpaRepository<LeadershipOfficial, UUID> {

    List<LeadershipOfficial> findAllByOrderByTeamAscSortOrderAscNameAsc();

    List<LeadershipOfficial> findAllByActiveTrueOrderBySortOrderAscNameAsc();

    List<LeadershipOfficial> findAllByTeamAndActiveTrueOrderBySortOrderAscNameAsc(LeadershipTeam team);
}
