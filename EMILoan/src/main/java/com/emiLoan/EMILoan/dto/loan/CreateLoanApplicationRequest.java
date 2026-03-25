package com.emiLoan.EMILoan.dto.loan;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateLoanApplicationRequest {

    @NotNull
    private UUID userId;

    @DecimalMin("0.01")
    private BigDecimal requestedAmount;

    @DecimalMin("0.01")
    private BigDecimal interestRate;

    @Min(1)
    private Integer tenureMonths;

    @DecimalMin("0.0")
    private BigDecimal existingEmi;
}