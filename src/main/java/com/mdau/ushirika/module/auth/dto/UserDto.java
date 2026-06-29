package com.mdau.ushirika.module.auth.dto;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.auth.enums.OfficialTitle;
import com.mdau.ushirika.module.auth.enums.UserRole;

import java.util.UUID;

public record UserDto(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phone,
        UserRole role,
        OfficialTitle officialTitle,
        boolean emailVerified,
        boolean active
) {
    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getRole(),
                user.getOfficialTitle(),
                user.isEmailVerified(),
                user.isActive()
        );
    }
}
