package com.mdau.ushirika.module.member.service;

import com.mdau.ushirika.common.exception.BadRequestException;
import com.mdau.ushirika.common.exception.ConflictException;
import com.mdau.ushirika.common.exception.ForbiddenException;
import com.mdau.ushirika.common.exception.ResourceNotFoundException;
import com.mdau.ushirika.common.response.PagedResponse;
import com.mdau.ushirika.module.auth.dto.UserDto;
import com.mdau.ushirika.module.auth.dto.UserProfileDto;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.auth.enums.UserRole;
import com.mdau.ushirika.module.auth.repository.UserRepository;
import com.mdau.ushirika.module.member.dto.CreateMemberRequest;
import com.mdau.ushirika.module.member.dto.UpdateMemberTierRequest;
import com.mdau.ushirika.module.member.dto.UpdateRoleRequest;
import com.mdau.ushirika.module.member.entity.MemberProfile;
import com.mdau.ushirika.module.member.enums.Gender;
import com.mdau.ushirika.module.member.repository.MemberProfileRepository;
import com.mdau.ushirika.module.dues.service.MembershipDuesService;
import com.mdau.ushirika.module.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final MemberProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final MembershipDuesService membershipDuesService;

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

    /**
     * Update a member's contribution plan tier (Standard / Family / Patron).
     * Only meaningful for users who have an approved MemberProfile.
     */
    @Transactional
    public UserProfileDto updateTier(UUID userId, UpdateMemberTierRequest req) {
        User target = findById(userId);
        MemberProfile profile = profileRepository.findByUser(target)
                .orElseThrow(() -> new BadRequestException(
                        "This user does not have an approved member profile yet. " +
                        "Tier can only be set after membership approval."));

        profile.setMembershipTier(req.tier());
        profileRepository.save(profile);
        return UserProfileDto.from(target, profile);
    }

    /**
     * Admin-initiated member creation. Bypasses the normal application flow.
     * Creates a fully verified, active member account and emails the credentials
     * to the member with a mandatory password-change notice.
     */
    @Transactional
    public UserProfileDto createMember(CreateMemberRequest req) {
        if (userRepository.existsByEmail(req.email().toLowerCase())) {
            throw new ConflictException("An account with this email already exists.");
        }
        if (userRepository.existsByPhone(req.phone())) {
            throw new ConflictException("An account with this phone number already exists.");
        }

        String tempPassword = generateTempPassword();

        User user = User.builder()
                .firstName(req.firstName())
                .lastName(req.lastName())
                .email(req.email().toLowerCase())
                .phone(req.phone())
                .password(passwordEncoder.encode(tempPassword))
                .emailVerified(true)
                .active(true)
                .build();
        userRepository.save(user);

        String memberId = generateMemberId();
        String tier = (req.tier() != null && !req.tier().isBlank()) ? req.tier() : "Standard";

        // Use placeholder values for required profile fields the admin doesn't supply.
        // The member is prompted to update these on their first login.
        String placeholderId = "P-" + user.getId().toString().replace("-", "").substring(0, 18);
        MemberProfile profile = MemberProfile.builder()
                .user(user)
                .idNumber(placeholderId)
                .dateOfBirth(LocalDate.of(1900, 1, 1))
                .gender(Gender.PREFER_NOT_TO_SAY)
                .address("Pending — please update after first login")
                .county("Pending")
                .nextOfKinName("Pending")
                .nextOfKinPhone("Pending")
                .nextOfKinRelationship("Pending")
                .memberId(memberId)
                .memberSince(LocalDate.now())
                .membershipTier(tier)
                .build();
        profileRepository.save(profile);
        membershipDuesService.createInitialDues(user);

        try {
            sendWelcomeCredentials(user.getEmail(), user.getFirstName(), memberId, tempPassword);
        } catch (Exception e) {
            log.warn("Welcome email failed for {} — account created, credentials must be shared manually: {}", user.getEmail(), e.getMessage());
        }

        return UserProfileDto.from(user, profile);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private String generateMemberId() {
        int year = LocalDate.now().getYear();
        long sequence = profileRepository.countByMemberIdNotNull() + 1;
        return "UW-%d-%04d".formatted(year, sequence);
    }

    private String generateTempPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789@#$!";
        SecureRandom rand = new SecureRandom();
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void sendWelcomeCredentials(String toEmail, String firstName, String memberId, String tempPassword) {
        String subject = "Welcome to Ushirika Welfare Foundation — Your Member Account";
        String html = """
                <div style="font-family:sans-serif;max-width:560px;margin:auto;color:#1a1a1a">
                  <h2 style="color:#1a6b3c">Welcome, %s!</h2>
                  <p>Your Ushirika Welfare Foundation member account has been created by the administrator. You can now sign in to your member portal.</p>
                  <table style="border-collapse:collapse;width:100%%;margin:24px 0;border:1px solid #e5e7eb;border-radius:8px">
                    <tr style="background:#f9fafb">
                      <td style="padding:12px 16px;font-weight:600;width:160px">Member ID</td>
                      <td style="padding:12px 16px;font-family:monospace;font-weight:700">%s</td>
                    </tr>
                    <tr>
                      <td style="padding:12px 16px;font-weight:600;border-top:1px solid #e5e7eb">Login Email</td>
                      <td style="padding:12px 16px;border-top:1px solid #e5e7eb">%s</td>
                    </tr>
                    <tr style="background:#f9fafb">
                      <td style="padding:12px 16px;font-weight:600;border-top:1px solid #e5e7eb">Temporary Password</td>
                      <td style="padding:12px 16px;border-top:1px solid #e5e7eb;font-family:monospace;font-weight:700;font-size:16px">%s</td>
                    </tr>
                  </table>
                  <div style="padding:16px;background:#fff3cd;border-left:4px solid #ffc107;border-radius:4px;margin-bottom:24px">
                    <strong>Important:</strong> This is a temporary password. Please change it immediately after your first login via <strong>Settings → Change Password</strong>.
                  </div>
                  <p><a href="https://ushirikacommunity.site/login" style="display:inline-block;padding:12px 24px;background:#1a6b3c;color:#fff;text-decoration:none;border-radius:24px;font-weight:600">Sign in to your portal</a></p>
                  <p style="margin-top:24px;color:#666;font-size:13px">Questions? Contact us at <a href="mailto:admin@ushirikawelfare.org">admin@ushirikawelfare.org</a></p>
                </div>
                """.formatted(firstName, memberId, toEmail, tempPassword);
        emailService.sendPlain(toEmail, firstName, subject, html);
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
