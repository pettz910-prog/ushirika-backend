package com.mdau.ushirika.module.member.service;

import com.mdau.ushirika.common.exception.BadRequestException;
import com.mdau.ushirika.common.exception.ForbiddenException;
import com.mdau.ushirika.common.exception.ResourceNotFoundException;
import com.mdau.ushirika.common.response.PagedResponse;
import com.mdau.ushirika.module.auth.dto.UserDto;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.auth.enums.UserRole;
import com.mdau.ushirika.module.auth.repository.UserRepository;
import com.mdau.ushirika.module.member.dto.UpdateRoleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PagedResponse<UserDto> listUsers(Pageable pageable) {
        return PagedResponse.of(userRepository.findAll(pageable).map(UserDto::from));
    }

    @Transactional(readOnly = true)
    public UserDto getUser(UUID userId) {
        return UserDto.from(findById(userId));
    }

    /**
     * SUPERADMIN can change any user's role and official title.
     * Guards:
     * - Cannot demote or modify another SUPERADMIN
     * - Cannot promote anyone to SUPERADMIN (only one CEO seat)
     */
    @Transactional
    public UserDto updateRole(UUID userId, UpdateRoleRequest req) {
        User target = findById(userId);
        User actor = currentUser();

        if (target.getId().equals(actor.getId())) {
            throw new BadRequestException("You cannot change your own role.");
        }

        if (target.getRole() == UserRole.SUPERADMIN) {
            throw new ForbiddenException("The SUPERADMIN role cannot be modified.");
        }

        if (req.role() == UserRole.SUPERADMIN) {
            throw new ForbiddenException("Cannot promote a user to SUPERADMIN. There can only be one.");
        }

        target.setRole(req.role());
        target.setOfficialTitle(req.officialTitle());
        userRepository.save(target);
        return UserDto.from(target);
    }

    /**
     * Activate or deactivate a user account.
     * Deactivated accounts cannot log in (isEnabled() = false).
     */
    @Transactional
    public UserDto setActive(UUID userId, boolean active) {
        User target = findById(userId);
        User actor = currentUser();

        if (target.getId().equals(actor.getId())) {
            throw new BadRequestException("You cannot deactivate your own account.");
        }

        if (target.getRole() == UserRole.SUPERADMIN) {
            throw new ForbiddenException("The SUPERADMIN account cannot be deactivated.");
        }

        target.setActive(active);
        userRepository.save(target);
        return UserDto.from(target);
    }

    private User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found."));
    }
}
