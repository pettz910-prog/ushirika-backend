package com.mdau.ushirika.config;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.auth.enums.UserRole;
import com.mdau.ushirika.module.auth.repository.UserRepository;
import com.mdau.ushirika.module.member.entity.MemberProfile;
import com.mdau.ushirika.module.member.enums.Gender;
import com.mdau.ushirika.module.member.enums.MaritalStatus;
import com.mdau.ushirika.module.member.repository.MemberProfileRepository;
import com.mdau.ushirika.module.payment.entity.ContributionPlan;
import com.mdau.ushirika.module.payment.enums.ContributionFrequency;
import com.mdau.ushirika.module.payment.repository.ContributionPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final ContributionPlanRepository planRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.superadmin.email:admin@ushirikawelfare.org}")
    private String superAdminEmail;

    @Value("${app.superadmin.password:Admin@1234}")
    private String superAdminPassword;

    @Value("${app.superadmin.phone:+254000000000}")
    private String superAdminPhone;

    @Value("${app.test-member.email:member@ushirikawelfare.org}")
    private String testMemberEmail;

    @Value("${app.test-member.password:Member@2025!}")
    private String testMemberPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedSuperAdmin();
        seedTestMember();
        seedContributionPlans();
    }

    private void seedSuperAdmin() {
        if (userRepository.existsByRole(UserRole.SUPERADMIN)) return;

        User superAdmin = User.builder()
                .firstName("Super")
                .lastName("Admin")
                .email(superAdminEmail)
                .phone(superAdminPhone)
                .password(passwordEncoder.encode(superAdminPassword))
                .role(UserRole.SUPERADMIN)
                .emailVerified(true)
                .active(true)
                .build();

        userRepository.save(superAdmin);
        log.warn("========================================================");
        log.warn("  SUPERADMIN created: {}", superAdminEmail);
        log.warn("  Change the default password immediately via /auth/reset-password");
        log.warn("========================================================");
    }

    private void seedTestMember() {
        if (userRepository.existsByEmail(testMemberEmail)) return;

        User member = User.builder()
                .firstName("Wekesa")
                .lastName("Wanjala")
                .email(testMemberEmail)
                .phone("+14695550142")
                .password(passwordEncoder.encode(testMemberPassword))
                .role(UserRole.MEMBER)
                .emailVerified(true)
                .active(true)
                .build();

        member = userRepository.save(member);

        MemberProfile profile = MemberProfile.builder()
                .user(member)
                .idNumber("TEST00000001")
                .dateOfBirth(LocalDate.of(1988, 4, 15))
                .gender(Gender.MALE)
                .address("6702 Ambercrest Dr, Arlington, TX 76002")
                .county("Vihiga")
                .maritalStatus(MaritalStatus.MARRIED)
                .spouseName("Aisha Wanjala")
                .nextOfKinName("Peter Wanjala")
                .nextOfKinPhone("+14695550143")
                .nextOfKinRelationship("Sibling")
                .emergencyContactName("Aisha Wanjala")
                .emergencyContactPhone("+14695550144")
                .occupation("Registered Nurse")
                .employer("Texas Health Resources")
                .heardAboutUs("Friend or member")
                .memberId("UW-2025-0001")
                .memberSince(LocalDate.of(2022, 3, 14))
                .membershipTier("Family")
                .build();

        memberProfileRepository.save(profile);
        log.info("Test member seeded: {} / password configured via app.test-member.password", testMemberEmail);
    }

    private void seedContributionPlans() {
        if (planRepository.existsByName("Standard")) return;

        ContributionPlan standard = ContributionPlan.builder()
                .name("Standard")
                .description("Individual membership — for a single Luhya community member.")
                .amount(new BigDecimal("25.00"))
                .currency("USD")
                .frequency(ContributionFrequency.MONTHLY)
                .features(List.of(
                        "Full bereavement support",
                        "Welfare fund access",
                        "Annual Family Day attendance",
                        "Community voting rights",
                        "Scholarship fund eligibility",
                        "Member directory listing"
                ))
                .badge(null)
                .displayOrder(1)
                .active(true)
                .build();

        ContributionPlan family = ContributionPlan.builder()
                .name("Family")
                .description("Household membership — covers member, spouse, and children under 18.")
                .amount(new BigDecimal("50.00"))
                .currency("USD")
                .frequency(ContributionFrequency.MONTHLY)
                .features(List.of(
                        "Everything in Standard",
                        "Spouse fully covered",
                        "Children under 18 covered",
                        "Double bereavement payout",
                        "Priority welfare queue",
                        "Family Day group tickets"
                ))
                .badge("Most Common")
                .displayOrder(2)
                .active(true)
                .build();

        ContributionPlan patron = ContributionPlan.builder()
                .name("Patron")
                .description("Extended family membership — covers member, spouse, children, and up to two additional relatives.")
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .frequency(ContributionFrequency.MONTHLY)
                .features(List.of(
                        "Everything in Family",
                        "Up to 2 additional relatives covered",
                        "Named in Annual Community Report",
                        "Advisory board eligibility",
                        "Scholarship nomination rights",
                        "VIP Family Day seating"
                ))
                .badge("Extended Family")
                .displayOrder(3)
                .active(true)
                .build();

        planRepository.saveAll(List.of(standard, family, patron));
        log.info("Seeded 3 default contribution plans: Standard $25, Family $50, Patron $100");
    }
}
