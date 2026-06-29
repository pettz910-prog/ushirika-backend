package com.mdau.ushirika.common.service;

import com.mdau.ushirika.module.auth.enums.UserRole;
import com.mdau.ushirika.module.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Shared quorum approval logic used by membership, welfare, and scholarship modules.
 *
 * Rules:
 *   - Any single REJECTED vote → immediately REJECTED (rejector stays anonymous)
 *   - All current ADMINs have voted APPROVED → APPROVED
 *   - Falls back to SUPERADMIN count if no ADMINs exist yet
 *   - Otherwise → PENDING (still under review)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuorumApprovalService {

    private final UserRepository userRepository;

    public enum QuorumResult {
        APPROVED,
        REJECTED,
        PENDING
    }

    /**
     * Evaluate quorum state given current vote tallies.
     *
     * @param approvedCount number of APPROVED votes cast so far
     * @param rejectedCount number of REJECTED votes cast so far
     * @return APPROVED, REJECTED, or PENDING
     */
    public QuorumResult evaluate(long approvedCount, long rejectedCount) {
        if (rejectedCount > 0) {
            return QuorumResult.REJECTED;
        }

        long adminCount = userRepository.countByRole(UserRole.ADMIN);
        if (adminCount == 0) {
            // No ADMINs seeded yet — SUPERADMIN vote alone is sufficient
            adminCount = userRepository.countByRole(UserRole.SUPERADMIN);
        }

        if (adminCount > 0 && approvedCount >= adminCount) {
            return QuorumResult.APPROVED;
        }

        log.debug("Quorum pending — approved: {}/{}", approvedCount, adminCount);
        return QuorumResult.PENDING;
    }
}
