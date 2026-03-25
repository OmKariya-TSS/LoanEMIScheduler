package com.emiLoan.EMILoan.dto.emi;

import com.emiLoan.EMILoan.common.enums.EmiStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class EmiResponse {

    private UUID emiId;
    private Integer installmentNo;
    private LocalDate dueDate;

    private BigDecimal principalComponent;
    private BigDecimal interestComponent;
    private BigDecimal totalEmi;

    private BigDecimal remainingBalance;

    private EmiStatus status;
    private LocalDate paidDate;
}