package com.mdau.ushirika.module.auth.controller;

import com.mdau.ushirika.common.exception.ResourceNotFoundException;
import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.module.auth.dto.UpdateCredentialsRequest;
import com.mdau.ushirika.module.auth.dto.UserProfileDto;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.auth.enums.UserRole;
import com.mdau.ushirika.module.auth.repository.UserRepository;
import com.mdau.ushirika.module.auth.service.AuthService;
import com.mdau.ushirika.module.dues.service.MembershipDuesService;
import com.mdau.ushirika.module.member.entity.MemberProfile;
import com.mdau.ushirika.module.member.repository.MemberProfileRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Authenticated user profile")
public class UserController {

    private final UserRepository userRepository;
    private final MemberProfileRepository profileRepository;
    private final MembershipDuesService duesService;
    private final AuthService authService;

    @GetMapping("/me")
    @Operation(
        summary = "Get the authenticated user's full profile",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<UserProfileDto>> me() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found."));
        MemberProfile profile = profileRepository.findByUser(user).orElse(null);

        // Resolve dues status only for approved members
        String duesStatus = null;
        if (user.getRole() == UserRole.MEMBER && profile != null && profile.getMemberId() != null) {
            duesStatus = duesService.getCurrentYearStatus(user)
                    .map(Enum::name)
                    .orElse(null);
        }

        return ResponseEntity.ok(ApiResponse.ok("Profile fetched", UserProfileDto.from(user, profile, duesStatus)));
    }

    @PutMapping("/me/credentials")
    @Operation(
        summary = "Update login email and/or password — current password required",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<Void>> updateCredentials(@Valid @RequestBody UpdateCredentialsRequest req) {
        authService.updateCredentials(req);
        return ResponseEntity.ok(ApiResponse.ok("Credentials updated. Please sign in with your new details."));
    }
}
