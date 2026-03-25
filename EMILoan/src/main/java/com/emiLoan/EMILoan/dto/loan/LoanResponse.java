package com.emiLoan.EMILoan.dto.loan;

import com.emiLoan.EMILoan.common.enums.LoanStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class LoanResponse {

    private UUID loanId;
    private String loanCode;
    private UUID applicationId;

    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private Integer tenureMonths;

    private BigDecimal emiAmount;

    private LocalDate startDate;
    private LocalDate endDate;

    private LoanStatus loanStatus;
}