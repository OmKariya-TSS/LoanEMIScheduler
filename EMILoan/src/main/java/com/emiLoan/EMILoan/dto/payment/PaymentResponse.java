package com.emiLoan.EMILoan.dto.payment;

import com.emiLoan.EMILoan.common.enums.PaymentMode;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PaymentResponse {

    private UUID paymentId;
    private UUID loanId;
    private UUID emiId;

    private BigDecimal amountPaid;
    private PaymentMode paymentMode;
//    private PaymentStatus status;

    private LocalDateTime paymentDate;
}