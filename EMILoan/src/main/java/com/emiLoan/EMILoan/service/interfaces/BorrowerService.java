package com.emiLoan.EMILoan.service.interfaces;


import com.emiLoan.EMILoan.dto.user.response.BorrowerDashboardResponse;
import com.emiLoan.EMILoan.dto.user.response.BorrowerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface BorrowerService {
    BorrowerResponse getProfile();

    BorrowerResponse updateFinancialProfile(BigDecimal newMonthlyIncome);

    BorrowerDashboardResponse getDashboardStats();

    BorrowerResponse getProfileByUserCode(String userCode);

    Page<BorrowerResponse> getAllBorrowers(Pageable pageable);
}

