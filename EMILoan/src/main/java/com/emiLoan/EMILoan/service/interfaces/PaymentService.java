package com.emiLoan.EMILoan.service.interfaces;

import com.emiLoan.EMILoan.dto.payment.ForeclosureRequest;
import com.emiLoan.EMILoan.dto.payment.PaymentHistoryResponse;
import com.emiLoan.EMILoan.dto.payment.PaymentRequest;
import com.emiLoan.EMILoan.dto.payment.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentService {

    PaymentResponse makePayment(PaymentRequest request,String email);

    Page<PaymentHistoryResponse> getAllPayments(String email,Pageable pageable);

    PaymentHistoryResponse getPaymentHistory(String loanCode, String requesterEmail);

    PaymentResponse forecloseLoan(ForeclosureRequest request, String email);

    Page<PaymentHistoryResponse> getBorrowerPaymentHistory(String borrowerEmail, Pageable pageable);
}