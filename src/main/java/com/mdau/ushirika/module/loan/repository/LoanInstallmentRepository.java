package com.mdau.ushirika.module.loan.repository;

import com.mdau.ushirika.module.loan.entity.LoanApplication;
import com.mdau.ushirika.module.loan.entity.LoanInstallment;
import com.mdau.ushirika.module.loan.enums.InstallmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, UUID> {

    List<LoanInstallment> findByLoanOrderByInstallmentNumber(LoanApplication loan);

    long countByLoanAndStatusNotIn(LoanApplication loan, List<InstallmentStatus> excludedStatuses);

    @Query("SELECT COALESCE(SUM(i.amountPaid), 0) FROM LoanInstallment i WHERE i.loan = :loan")
    java.math.BigDecimal sumAmountPaidByLoan(@Param("loan") LoanApplication loan);
}
