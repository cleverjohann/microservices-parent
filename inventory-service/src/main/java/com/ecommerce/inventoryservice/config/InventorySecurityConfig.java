package com.ecommerce.inventoryservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class InventorySecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // Solo APIs REST - no sessiones ni cookies usamos CSRF
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // no va a guardar ninguna sesión de usuario, para q sea por token
                .authorizeHttpRequests(authz -> authz //reglas de autentifiación
                        .requestMatchers("/actuator/**").permitAll() // endpoints públicos
                        .requestMatchers("/api/inventory/**").permitAll() // desarrollo
                        .anyRequest().authenticated() // resto autenticado
                );
        return http.build();
    }
}
