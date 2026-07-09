package com.mdau.ushirika.module.leadership.dto;

import java.util.List;

public record PublicLeadershipDto(
        List<LeadershipOfficialDto> executive,
        List<LeadershipOfficialDto> hospitality,
        List<LeadershipOfficialDto> compliance
) {}
