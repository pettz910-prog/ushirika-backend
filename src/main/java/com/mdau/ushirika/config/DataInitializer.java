package com.mdau.ushirika.config;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.auth.enums.UserRole;
import com.mdau.ushirika.module.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.superadmin.email:admin@ushirikawelfare.org}")
    private String superAdminEmail;

    @Value("${app.superadmin.password:Admin@1234}")
    private String superAdminPassword;

    @Value("${app.superadmin.phone:+254000000000}")
    private String superAdminPhone;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!userRepository.existsByRole(UserRole.SUPERADMIN)) {
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
    }
}
