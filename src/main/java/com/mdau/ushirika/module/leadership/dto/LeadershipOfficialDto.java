package com.mdau.ushirika.module.leadership.dto;

import com.mdau.ushirika.module.leadership.entity.LeadershipOfficial;
import com.mdau.ushirika.module.leadership.enums.LeadershipTeam;

import java.util.UUID;

public record LeadershipOfficialDto(
        UUID id,
        String name,
        String role,
        LeadershipTeam team,
        String bio,
        String imageUrl,
        boolean active,
        int sortOrder
) {
    public static LeadershipOfficialDto from(LeadershipOfficial o) {
        return new LeadershipOfficialDto(
                o.getId(),
                o.getName(),
                o.getRole(),
                o.getTeam(),
                o.getBio(),
                o.getImageUrl(),
                o.isActive(),
                o.getSortOrder()
        );
    }
}
