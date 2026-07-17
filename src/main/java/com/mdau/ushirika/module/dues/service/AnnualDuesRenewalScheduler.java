package com.mdau.ushirika.module.dues.service;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.auth.repository.UserRepository;
import com.mdau.ushirika.module.dues.entity.MembershipDue;
import com.mdau.ushirika.module.dues.enums.DuesStatus;
import com.mdau.ushirika.module.dues.repository.MembershipDueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * On January 1 of each year, creates a $100 dues record (due October 31) for
 * every active member who does not already have one for that year.
 * This covers Year 2+ renewals — Year 1 is created at approval time by
 * {@link MembershipDuesService#createInitialDues(User)}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnnualDuesRenewalScheduler {

    private final MembershipDueRepository dueRepository;
    private final UserRepository          userRepository;

    @Scheduled(cron = "0 0 1 1 1 *")
    @Transactional
    public void createAnnualDues() {
        int year = LocalDate.now().getYear();
        LocalDate dueDate = LocalDate.of(year, 10, 31);

        List<User> activeMembers = userRepository.findAllByActiveTrue();

        int created = 0;
        for (User user : activeMembers) {
            if (dueRepository.findByUserAndYear(user, year).isPresent()) continue;

            dueRepository.save(MembershipDue.builder()
                    .user(user)
                    .year(year)
                    .amount(MembershipDuesService.ANNUAL_FEE)
                    .dueDate(dueDate)
                    .status(DuesStatus.PENDING)
                    .build());
            created++;
        }
        log.info("AnnualDuesRenewalScheduler: created {} dues records for year {}", created, year);
    }
}
