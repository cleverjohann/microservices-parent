package com.ecommerce.authservice.service;

import com.ecommerce.authservice.dto.AuthResponse;
import com.ecommerce.authservice.dto.LoginRequest;
import com.ecommerce.authservice.dto.RegisterRequest;
import com.ecommerce.authservice.models.User;
import com.ecommerce.sharedlib.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Value("${jwt.expiration:86400000}") // 24 horas en milisegundos
    private Long jwtExpiration;

    /**
     * Procesa el login del usuario
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Processing login for email: {}", request.getEmail());

        // Validar credenciales
        if (!userService.validateCredentials(request.getEmail(), request.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // Obtener usuario
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Generar token
        List<String> roles = userService.getUserRoles(user);
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), roles); // Pasar también el ID

        log.info("Login successful for user: {}", request.getEmail());

        // Crear respuesta
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(roles)
                .expiresIn(jwtExpiration / 1000) // Convertir a segundos
                .build();
    }

    /**
     * Procesa el registro de un nuevo usuario
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("Processing registration for email: {}", request.getEmail());

        // Crear usuario
        User newUser = userService.createUser(request);

        // Generar token para el nuevo usuario - AQUÍ VA EL OTRO CAMBIO
        List<String> roles = userService.getUserRoles(newUser);
        String token = jwtUtil.generateToken(newUser.getEmail(), newUser.getId(), roles); // Pasar también el ID

        log.info("Registration successful for user: {}", request.getEmail());

        // Crear respuesta
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .email(newUser.getEmail())
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .roles(roles)
                .expiresIn(jwtExpiration / 1000) // Convertir a segundos
                .build();
    }

}
