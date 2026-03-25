package com.emiLoan.EMILoan.dto.payment;

import com.emiLoan.EMILoan.common.enums.PaymentMode;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreatePaymentRequest {

    @NotNull
    private UUID loanId;

    @NotNull
    private UUID emiId;

    @DecimalMin("0.01")
    private BigDecimal amountPaid;

    @NotNull
    private PaymentMode paymentMode;
}