package com.mdau.ushirika.module.dashboard.service;

import com.mdau.ushirika.module.auth.enums.UserRole;
import com.mdau.ushirika.module.auth.repository.UserRepository;
import com.mdau.ushirika.module.content.enums.ArticleStatus;
import com.mdau.ushirika.module.content.repository.ArticleRepository;
import com.mdau.ushirika.module.content.repository.MediaAssetRepository;
import com.mdau.ushirika.module.dashboard.dto.DashboardSummaryDto;
import com.mdau.ushirika.module.dashboard.dto.DashboardSummaryDto.*;
import com.mdau.ushirika.module.dashboard.dto.MonthlySeriesDto;
import com.mdau.ushirika.module.dashboard.dto.MonthlySeriesDto.MonthlyPoint;
import com.mdau.ushirika.module.dashboard.dto.ScholarshipBreakdownDto;
import com.mdau.ushirika.module.dashboard.dto.ScholarshipBreakdownDto.ProgramRow;
import com.mdau.ushirika.module.dashboard.dto.WelfareBreakdownDto;
import com.mdau.ushirika.module.dashboard.dto.WelfareBreakdownDto.CategoryRow;
import com.mdau.ushirika.module.donation.enums.CampaignStatus;
import com.mdau.ushirika.module.donation.enums.DonationStatus;
import com.mdau.ushirika.module.donation.repository.DonationCampaignRepository;
import com.mdau.ushirika.module.donation.repository.DonationRepository;
import com.mdau.ushirika.module.event.enums.EventStatus;
import com.mdau.ushirika.module.event.repository.EventRegistrationRepository;
import com.mdau.ushirika.module.event.repository.EventRepository;
import com.mdau.ushirika.module.member.enums.ApplicationStatus;
import com.mdau.ushirika.module.member.repository.MembershipApplicationRepository;
import com.mdau.ushirika.module.notification.enums.NotificationChannel;
import com.mdau.ushirika.module.notification.enums.NotificationStatus;
import com.mdau.ushirika.module.notification.repository.NotificationLogRepository;
import com.mdau.ushirika.module.payment.repository.MemberContributionRepository;
import com.mdau.ushirika.module.scholarship.enums.ScholarshipApplicationStatus;
import com.mdau.ushirika.module.scholarship.repository.ScholarshipApplicationRepository;
import com.mdau.ushirika.module.scholarship.repository.ScholarshipAwardRepository;
import com.mdau.ushirika.module.scholarship.repository.ScholarshipProgramRepository;
import com.mdau.ushirika.module.welfare.enums.WelfareRequestStatus;
import com.mdau.ushirika.module.welfare.repository.WelfareCategoryRepository;
import com.mdau.ushirika.module.welfare.repository.WelfareDisbursementRepository;
import com.mdau.ushirika.module.welfare.repository.WelfareRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository                   userRepository;
    private final MembershipApplicationRepository  memberApplicationRepository;
    private final WelfareRequestRepository         welfareRequestRepository;
    private final WelfareDisbursementRepository    welfareDisbursementRepository;
    private final WelfareCategoryRepository        welfareCategoryRepository;
    private final ScholarshipApplicationRepository scholarshipApplicationRepository;
    private final ScholarshipAwardRepository       scholarshipAwardRepository;
    private final ScholarshipProgramRepository     scholarshipProgramRepository;
    private final MemberContributionRepository     contributionRepository;
    private final DonationRepository               donationRepository;
    private final DonationCampaignRepository       campaignRepository;
    private final EventRepository                  eventRepository;
    private final EventRegistrationRepository      registrationRepository;
    private final ArticleRepository                articleRepository;
    private final MediaAssetRepository             mediaAssetRepository;
    private final NotificationLogRepository        notificationLogRepository;

    // ─────────────────────────────────────── Main dashboard

    @Transactional(readOnly = true)
    public DashboardSummaryDto getDashboard() {
        return new DashboardSummaryDto(
                memberStats(),
                welfareStats(),
                scholarshipStats(),
                financialStats(),
                donationStats(),
                eventStats(),
                contentStats(),
                notificationStats()
        );
    }

    // ─────────────────────────────────────── Reports

    @Transactional(readOnly = true)
    public MonthlySeriesDto getFinancialSeries(int months) {
        LocalDateTime from = LocalDateTime.now().minusMonths(months).withDayOfMonth(1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        List<MonthlyPoint> contributions = toPoints(contributionRepository.monthlyTotals(from));
        List<MonthlyPoint> donations     = toPoints(donationRepository.monthlyTotals(from));

        return new MonthlySeriesDto(contributions, donations);
    }

    @Transactional(readOnly = true)
    public WelfareBreakdownDto getWelfareBreakdown() {
        List<CategoryRow> rows = welfareCategoryRepository.findAll()
                .stream()
                .map(cat -> {
                    List<com.mdau.ushirika.module.welfare.entity.WelfareRequest> reqs =
                            welfareRequestRepository
                                    .findAllByOrderByCreatedAtDesc(
                                            org.springframework.data.domain.Pageable.unpaged())
                                    .stream()
                                    .filter(r -> r.getCategory() != null
                                                 && r.getCategory().getId().equals(cat.getId()))
                                    .toList();

                    return new CategoryRow(
                            cat.getName(),
                            reqs.size(),
                            reqs.stream().filter(r -> r.getStatus() == WelfareRequestStatus.APPROVED).count(),
                            reqs.stream().filter(r -> r.getStatus() == WelfareRequestStatus.DISBURSED).count(),
                            reqs.stream().filter(r -> r.getStatus() == WelfareRequestStatus.REJECTED).count(),
                            reqs.stream().filter(r -> r.getStatus() == WelfareRequestStatus.DRAFT
                                    || r.getStatus() == WelfareRequestStatus.SUBMITTED).count()
                    );
                })
                .toList();

        return new WelfareBreakdownDto(rows, welfareDisbursementRepository.sumTotalDisbursed());
    }

    @Transactional(readOnly = true)
    public ScholarshipBreakdownDto getScholarshipBreakdown() {
        List<ProgramRow> rows = scholarshipProgramRepository.findAll()
                .stream()
                .map(prog -> {
                    var apps = scholarshipApplicationRepository
                            .findAllByOrderByCreatedAtDesc(
                                    org.springframework.data.domain.Pageable.unpaged())
                            .stream()
                            .filter(a -> a.getProgram().getId().equals(prog.getId()))
                            .toList();

                    return new ProgramRow(
                            prog.getName(),
                            prog.getAcademicYear(),
                            apps.size(),
                            apps.stream().filter(a -> a.getStatus() == ScholarshipApplicationStatus.APPROVED).count(),
                            apps.stream().filter(a -> a.getStatus() == ScholarshipApplicationStatus.AWARDED).count(),
                            apps.stream().filter(a -> a.getStatus() == ScholarshipApplicationStatus.REJECTED).count(),
                            apps.stream().filter(a -> a.getStatus() == ScholarshipApplicationStatus.DRAFT
                                    || a.getStatus() == ScholarshipApplicationStatus.SUBMITTED
                                    || a.getStatus() == ScholarshipApplicationStatus.UNDER_REVIEW).count()
                    );
                })
                .toList();

        return new ScholarshipBreakdownDto(rows, scholarshipAwardRepository.sumTotalAwarded());
    }

    // ─────────────────────────────────────── Private stat builders

    private MemberStats memberStats() {
        return new MemberStats(
                userRepository.countByRole(UserRole.MEMBER),
                // DRAFT + SUBMITTED = not yet under review
                memberApplicationRepository.countByStatus(ApplicationStatus.DRAFT)
                + memberApplicationRepository.countByStatus(ApplicationStatus.SUBMITTED),
                // Form sent — applicant is onboarding or has submitted their registration payment
                memberApplicationRepository.countByStatus(ApplicationStatus.FORM_SENT)
                + memberApplicationRepository.countByStatus(ApplicationStatus.ONBOARDING_IN_PROGRESS)
                + memberApplicationRepository.countByStatus(ApplicationStatus.PAYMENT_SUBMITTED),
                memberApplicationRepository.countByStatus(ApplicationStatus.APPROVED),
                memberApplicationRepository.countByStatus(ApplicationStatus.REJECTED)
        );
    }

    private WelfareStats welfareStats() {
        return new WelfareStats(
                welfareRequestRepository.count(),
                // DRAFT + SUBMITTED = awaiting review
                welfareRequestRepository.countByStatus(WelfareRequestStatus.DRAFT)
                + welfareRequestRepository.countByStatus(WelfareRequestStatus.SUBMITTED),
                welfareRequestRepository.countByStatus(WelfareRequestStatus.UNDER_REVIEW),
                welfareRequestRepository.countByStatus(WelfareRequestStatus.APPROVED),
                welfareRequestRepository.countByStatus(WelfareRequestStatus.DISBURSED),
                welfareRequestRepository.countByStatus(WelfareRequestStatus.REJECTED),
                welfareDisbursementRepository.sumTotalDisbursed()
        );
    }

    private ScholarshipStats scholarshipStats() {
        return new ScholarshipStats(
                scholarshipApplicationRepository.count(),
                scholarshipApplicationRepository.countByStatus(ScholarshipApplicationStatus.DRAFT)
                + scholarshipApplicationRepository.countByStatus(ScholarshipApplicationStatus.SUBMITTED),
                scholarshipApplicationRepository.countByStatus(ScholarshipApplicationStatus.UNDER_REVIEW),
                scholarshipApplicationRepository.countByStatus(ScholarshipApplicationStatus.APPROVED),
                scholarshipApplicationRepository.countByStatus(ScholarshipApplicationStatus.AWARDED),
                scholarshipApplicationRepository.countByStatus(ScholarshipApplicationStatus.REJECTED),
                scholarshipAwardRepository.sumTotalAwarded()
        );
    }

    private FinancialStats financialStats() {
        BigDecimal contributions = contributionRepository.sumAll();
        BigDecimal donations     = donationRepository.sumAllCompleted();
        return new FinancialStats(
                contributions,
                contributionRepository.count(),
                donations,
                donationRepository.countByStatus(DonationStatus.COMPLETED),
                contributions.add(donations)
        );
    }

    private DonationStats donationStats() {
        return new DonationStats(
                campaignRepository.countByStatus(CampaignStatus.ACTIVE),
                campaignRepository.count()
        );
    }

    private EventStats eventStats() {
        return new EventStats(
                eventRepository.count(),
                eventRepository.countByStatus(EventStatus.PUBLISHED),
                eventRepository.countByStatus(EventStatus.ONGOING),
                eventRepository.countByStatus(EventStatus.COMPLETED),
                registrationRepository.count()
        );
    }

    private ContentStats contentStats() {
        return new ContentStats(
                articleRepository.countByStatus(ArticleStatus.PUBLISHED),
                articleRepository.countByStatus(ArticleStatus.DRAFT),
                mediaAssetRepository.count()
        );
    }

    private NotificationStats notificationStats() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return new NotificationStats(
                notificationLogRepository.countByChannelAndStatusAndCreatedAtAfter(
                        NotificationChannel.EMAIL, NotificationStatus.SENT,    sevenDaysAgo),
                notificationLogRepository.countByChannelAndStatusAndCreatedAtAfter(
                        NotificationChannel.EMAIL, NotificationStatus.FAILED,  sevenDaysAgo),
                notificationLogRepository.countByChannelAndStatusAndCreatedAtAfter(
                        NotificationChannel.SMS,   NotificationStatus.SENT,    sevenDaysAgo),
                notificationLogRepository.countByChannelAndStatusAndCreatedAtAfter(
                        NotificationChannel.SMS,   NotificationStatus.FAILED,  sevenDaysAgo)
        );
    }

    // ─────────────────────────────────────── Helpers

    private List<MonthlyPoint> toPoints(List<Object[]> rows) {
        return rows.stream().map(row -> {
            int year   = ((Number) row[0]).intValue();
            int month  = ((Number) row[1]).intValue();
            BigDecimal amount = row[2] instanceof BigDecimal bd ? bd : BigDecimal.valueOf(((Number) row[2]).doubleValue());
            long count = ((Number) row[3]).longValue();
            String label = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + year;
            return new MonthlyPoint(year, month, label, amount, count);
        }).toList();
    }
}
