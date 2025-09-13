package com.ecommerce.sharedlib.constants;

public class SecurityContrants {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    // Endpoints públicos
    public static final String[] PUBLIC_URLS = {
            "/api/auth/**",
            "/h2-console/**",
            "/actuator/health",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };
}
