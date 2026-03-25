package com.emiLoan.EMILoan.dto.user;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class UserResponse {

    private UUID userId;
    private String userCode;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    private BigDecimal monthlyIncome;

    private Boolean enabled;
    private LocalDateTime createdAt;

    private Set<String> roles;
}