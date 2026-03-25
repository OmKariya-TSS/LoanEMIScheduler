package com.emiLoan.EMILoan.dto.auth;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class RegisterLoanOfficerRequest {

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
}