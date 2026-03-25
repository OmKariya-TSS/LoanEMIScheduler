package com.emiLoan.EMILoan.dto.auth;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class RegisterCustomerRequest {

    @NotBlank
    private String firstName;

    private String lastName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

    @Pattern(regexp = "^[0-9]{10,15}$")
    private String phone;

    @DecimalMin("0.0")
    private BigDecimal monthlyIncome;
}