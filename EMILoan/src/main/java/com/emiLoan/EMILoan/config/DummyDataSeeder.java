package com.emiLoan.EMILoan.config;


import com.emiLoan.EMILoan.common.enums.RoleName;
import com.emiLoan.EMILoan.entity.*;
import com.emiLoan.EMILoan.repository.*;
import com.emiLoan.EMILoan.utils.PANHashingUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class DummyDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PersonIdentityRepository personIdentityRepository;
    private final BorrowerProfileRepository borrowerProfileRepository;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final PANHashingUtil panHashingUtil;
    private final EntityManager entityManager;

    private final Faker faker = new Faker(new Locale("en", "IN"));
    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 5) {
            log.info("Skipping dummy data seeding: Database already populated.");
            return;
        }

        seedLoanOfficers(5);
        seedBorrowers(50);
    }

    private void seedLoanOfficers(int count) {
        Role officerRole = roleRepository.findByRoleName(RoleName.LOAN_OFFICER).get();
        log.info("Seeding {} Loan Officers...", count);

        for (int i = 0; i < count; i++) {
            User user = createBaseUser(officerRole);
            employeeProfileRepository.save(EmployeeProfile.builder()
                    .user(user)
                    .joiningDate(LocalDate.now().minusMonths(faker.number().numberBetween(1, 24)))
                    .salary(BigDecimal.valueOf(faker.number().numberBetween(40000, 80000)))
                    .isActive(true)
                    .build());
        }
    }

    private void seedBorrowers(int count) {
        Role borrowerRole = roleRepository.findByRoleName(RoleName.BORROWER).get();
        log.info("Seeding {} Borrowers for pagination...", count);

        for (int i = 0; i < count; i++) {
            User user = createBaseUser(borrowerRole);
            borrowerProfileRepository.save(BorrowerProfile.builder()
                    .user(user)
                    .monthlyIncome(BigDecimal.valueOf(faker.number().numberBetween(25000, 150000)))
                    .existingLoanCount(faker.number().numberBetween(0, 3))
                    .build());
        }
    }

    private User createBaseUser(Role role) {
        String dummyPan = faker.regexify("[A-Z]{5}[0-9]{4}[A-Z]{1}");
        String panHash = panHashingUtil.hash(dummyPan);

        PersonIdentity identity = personIdentityRepository.findByPanHash(panHash)
                .orElseGet(() -> {
                    PersonIdentity newPerson = PersonIdentity.builder()
                            .panHash(panHash)
                            .panFirst3(panHashingUtil.extractFirst3(dummyPan))
                            .panLast2(panHashingUtil.extractLast2(dummyPan))
                            .build();
                    return personIdentityRepository.saveAndFlush(newPerson);
                });

        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(faker.internet().emailAddress(firstName.toLowerCase() + "." + lastName.toLowerCase()))
                .password(passwordEncoder.encode("Password@123"))
                .phone(faker.phoneNumber().phoneNumber())
                .role(role)
                .person(identity)
                .isActive(true)
                .build();

        User savedUser = userRepository.saveAndFlush(user);
        entityManager.refresh(savedUser);
        return savedUser;
    }
}