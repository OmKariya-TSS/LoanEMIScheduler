package com.emiLoan.EMILoan.dto.loan;

import com.emiLoan.EMILoan.common.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class LoanApplicationResponse {

    private UUID applicationId;
    private String applicationCode;
    private UUID userId;

    private BigDecimal requestedAmount;
    private BigDecimal interestRate;
    private Integer tenureMonths;

    private BigDecimal existingEmi;
    private BigDecimal dtiRatio;

    private String suggestedStrategy;
    private String officerStrategy;

    private ApplicationStatus status;

    private LocalDateTime appliedAt;
}