package com.ecommerce.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
    private Long expiresIn; // en segundos
}
