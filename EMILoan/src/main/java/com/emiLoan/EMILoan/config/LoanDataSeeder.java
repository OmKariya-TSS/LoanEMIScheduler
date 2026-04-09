package com.emiLoan.EMILoan.config;


import com.emiLoan.EMILoan.common.enums.*;
import com.emiLoan.EMILoan.entity.*;
import com.emiLoan.EMILoan.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(3)
public class LoanDataSeeder implements CommandLineRunner {

    private final LoanApplicationRepository applicationRepository;
    private final LoanRepository loanRepository;
    private final EmiScheduleRepository emiRepository;
    private final UserRepository userRepository;
    private final BorrowerProfileRepository borrowerProfileRepository;

    private final Faker faker = new Faker(new Locale("en", "IN"));

    private final Random random = new Random();

    @Override
    @Transactional
    public void run(String... args) {
        if (loanRepository.count() > 0) {
            log.info("Skipping loan seeding: Loans already exist.");
            return;
        }

        List<User> borrowers = userRepository.findAllByRole_RoleName(RoleName.BORROWER);
        List<User> officers = userRepository.findAllByRole_RoleName(RoleName.LOAN_OFFICER);

        if (borrowers.isEmpty() || officers.isEmpty()) {
            log.error("Cannot seed loans: No borrowers or officers found in DB.");
            return;
        }

        log.info("🚀 Seeding Loan Applications, Active Loans, and EMI Schedules...");

        for (User borrower : borrowers) {
            seedApplicationsForBorrower(borrower, officers);
        }

        log.info("✅ Loan ecosystem seeding complete!");
    }

    private void seedApplicationsForBorrower(User borrower, List<User> officers) {
        createApplication(borrower, null, ApplicationStatus.PENDING);

        LoanApplication approvedApp = createApplication(borrower, officers.get(random.nextInt(officers.size())), ApplicationStatus.APPROVED);
        createActiveLoanWithEmis(approvedApp);

        if (random.nextBoolean()) {
            createApplication(borrower, officers.get(random.nextInt(officers.size())), ApplicationStatus.REJECTED);
        }
    }

    private LoanApplication createApplication(User borrower, User officer, ApplicationStatus status) {
        BigDecimal requested = BigDecimal.valueOf(faker.number().numberBetween(50000, 500000));

        LoanApplication app = LoanApplication.builder()
                .borrower(borrower)
                .requestedAmount(requested)
                .interestRate(BigDecimal.valueOf(10.5))
                .tenureMonths(faker.options().option(12, 24, 36))
                .status(status)
                .appliedAt(LocalDateTime.now().minusDays(faker.number().numberBetween(10, 60)))
                .reviewedBy(officer)
                .reviewedAt(status != ApplicationStatus.PENDING ? LocalDateTime.now().minusDays(2) : null)
                .suggestedStrategy("REDUCING_BALANCE")
                .dtiRatio(BigDecimal.valueOf(faker.number().randomDouble(2, 20, 45)))
                .build();

        return applicationRepository.save(app);
    }

    private void createActiveLoanWithEmis(LoanApplication app) {
        LocalDate startDate = LocalDate.now().minusMonths(3);
        Integer tenure = app.getTenureMonths();

        BigDecimal principal = app.getRequestedAmount();
        // Use a more robust base EMI calculation
        BigDecimal emiAmount = principal.divide(BigDecimal.valueOf(tenure), 2, RoundingMode.HALF_UP)
                .add(principal.multiply(BigDecimal.valueOf(0.01)));

        Loan loan = Loan.builder()
                .application(app)
                .borrower(app.getBorrower())
                .principalAmount(principal)
                .interestRate(app.getInterestRate())
                .tenureMonths(tenure)
                .strategy("REDUCING_BALANCE")
                .emiAmount(emiAmount)
                .startDate(startDate)
                .endDate(startDate.plusMonths(tenure))
                .loanStatus(LoanStatus.ACTIVE)
                .build();

        Loan savedLoan = loanRepository.save(loan);

        BigDecimal remainingPrincipal = principal;

        for (int i = 1; i <= tenure; i++) {
            // 1. If remaining principal is already 0, stop creating EMIs
            // (Prevents the 0.00 total_emi check violation)
            if (remainingPrincipal.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal interestComp = remainingPrincipal.multiply(BigDecimal.valueOf(0.008))
                    .setScale(2, RoundingMode.HALF_UP)
                    .max(BigDecimal.ZERO);

            BigDecimal principalComp = emiAmount.subtract(interestComp);

            // 2. Adjust for the final payment to ensure we don't overpay or leave a tiny balance
            if (i == tenure || principalComp.compareTo(remainingPrincipal) > 0) {
                principalComp = remainingPrincipal;
            }

            BigDecimal actualTotalEmi = principalComp.add(interestComp);

            // 3. Last stand safety: If total_emi is somehow still 0.00, don't save it
            if (actualTotalEmi.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            remainingPrincipal = remainingPrincipal.subtract(principalComp);

            EmiStatus status = (i <= 2) ? EmiStatus.PAID : EmiStatus.PENDING;

            EmiSchedule emi = EmiSchedule.builder()
                    .loan(savedLoan)
                    .installmentNo(i)
                    .dueDate(startDate.plusMonths(i))
                    .principalComponent(principalComp)
                    .interestComponent(interestComp)
                    .totalEmi(actualTotalEmi) // Set the sum of components
                    .remainingBalance(remainingPrincipal.max(BigDecimal.ZERO))
                    .status(status)
                    .amountPaid(status == EmiStatus.PAID ? actualTotalEmi : BigDecimal.ZERO)
                    .build();

            emiRepository.save(emi);
        }
    }
}