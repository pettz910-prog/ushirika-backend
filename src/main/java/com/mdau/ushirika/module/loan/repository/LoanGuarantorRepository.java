package com.mdau.ushirika.module.loan.repository;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.loan.entity.LoanApplication;
import com.mdau.ushirika.module.loan.entity.LoanGuarantor;
import com.mdau.ushirika.module.loan.enums.GuarantorStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoanGuarantorRepository extends JpaRepository<LoanGuarantor, UUID> {

    List<LoanGuarantor> findByLoan(LoanApplication loan);

    List<LoanGuarantor> findByGuarantorUserOrderByCreatedAtDesc(User guarantorUser);

    List<LoanGuarantor> findByGuarantorUserAndStatus(User guarantorUser, GuarantorStatus status);

    boolean existsByLoanAndGuarantorUser(LoanApplication loan, User guarantorUser);
}
