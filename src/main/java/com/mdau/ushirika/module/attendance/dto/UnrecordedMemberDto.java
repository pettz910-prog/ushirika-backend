package com.mdau.ushirika.module.attendance.dto;

import java.util.UUID;

public record UnrecordedMemberDto(
        UUID userId,
        String memberName,
        String email,
        String memberId
) {}
