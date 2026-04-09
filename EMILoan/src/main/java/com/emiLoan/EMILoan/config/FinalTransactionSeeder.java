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

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(4)
public class FinalTransactionSeeder implements CommandLineRunner {

    private final PaymentRepository paymentRepository;
    private final AuditLogRepository auditLogRepository;
    private final StrategyAuditRepository strategyAuditRepository;
    private final EmiScheduleRepository emiRepository;
    private final UserRepository userRepository;

    private final Faker faker = new Faker(new Locale("en", "IN"));
    @Override
    @Transactional
    public void run(String... args) {
        if (paymentRepository.count() > 0) return;

        log.info("💳 Seeding Payments, Strategy Audits, and System Audit Logs...");

        List<EmiSchedule> paidEmis = emiRepository.findByStatus(EmiStatus.PAID);
        User admin = userRepository.findByEmail("admin@loan.com").orElse(null);

        for (EmiSchedule emi : paidEmis) {
            Payment payment = Payment.builder()
                    .emiSchedule(emi)
                    .loan(emi.getLoan())
                    .amountPaid(emi.getPrincipalComponent().add(emi.getInterestComponent()))
                    .paymentMode(faker.options().option(PaymentMode.UPI, PaymentMode.NET_BANKING, PaymentMode.CARD))
                    .status(PaymentStatus.SUCCESS)
                    .paymentDate(emi.getDueDate().atTime(10, 30))
                    .build();
            paymentRepository.save(payment);

            seedAudit(null, AuditAction.UPDATE, AuditEntityType.PAYMENT, payment.getPaymentId(),
                    "Payment of " + payment.getAmountPaid() + " processed for EMI " + emi.getInstallmentNo());
        }

        List<EmiSchedule> randomEmis = paidEmis.subList(0, Math.min(paidEmis.size(), 5));
        for (EmiSchedule emi : randomEmis) {
            StrategyAudit strategy = StrategyAudit.builder()
                    .application(emi.getLoan().getApplication())
                    .systemStrategy("FLAT_RATE")
                    .officerStrategy("REDUCING_BALANCE")
                    .overridden(true)
                    .changedBy(admin)
                    .build();
            strategyAuditRepository.save(strategy);

            seedAudit(admin, AuditAction.UPDATE, AuditEntityType.APPLICATION,
                    strategy.getApplication().getApplicationId(), "Loan Strategy overridden by Admin");
        }

        log.info("✅ All systems seeded and ready for demo!");
    }

    private void seedAudit(User actor, AuditAction action, AuditEntityType type, UUID entityId, String desc) {
        AuditLog logEntry = AuditLog.builder()
                .actor(actor)
                .action(action)
                .entityType(type)
                .entityId(entityId)
                .description(desc)
                .build();
        auditLogRepository.save(logEntry);
    }
}
