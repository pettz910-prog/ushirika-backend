package com.mdau.ushirika.module.leadership.dto;

import com.mdau.ushirika.module.leadership.enums.LeadershipTeam;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SaveOfficialRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(max = 150) String role,
        @NotNull LeadershipTeam team,
        @Size(max = 1000) String bio,
        Integer sortOrder
) {}
