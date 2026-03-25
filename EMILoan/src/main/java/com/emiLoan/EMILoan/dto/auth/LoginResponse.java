package com.emiLoan.EMILoan.dto.auth;

import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class LoginResponse {

    private String accessToken;
    private String refreshToken;

    private String tokenType; // Bearer
    private Long expiresIn;

    private String userId;
    private String email;

    private Set<String> roles;
}