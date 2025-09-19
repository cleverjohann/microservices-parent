package com.ecommerce.authservice.service;

import com.ecommerce.authservice.dto.RegisterRequest;
import com.ecommerce.authservice.models.Role;
import com.ecommerce.authservice.models.User;
import com.ecommerce.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Busca un usuario por email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Crea un nuevo usuario
     */
    @Transactional
    public User createUser(RegisterRequest request) {
        log.info("Creating new user with email: {}", request.getEmail());

        // Verificar si el email ya existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email ya está registrado: " + request.getEmail());
        }

        // Crear el usuario
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of(Role.USER)) // Por defecto, rol USER
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());

        return savedUser;
    }

    /**
     * Verifica si las credenciales son válidas
     */
    public boolean validateCredentials(String email, String rawPassword) {
        Optional<User> userOpt = findByEmail(email);

        if (userOpt.isEmpty()) {
            log.warn("User not found with email: {}", email);
            return false;
        }

        User user = userOpt.get();

        if (!user.getEnabled()) {
            log.warn("User account is disabled: {}", email);
            return false;
        }

        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
        log.info("Password validation for {}: {}", email, matches ? "SUCCESS" : "FAILED");

        return matches;
    }

    /**
     * Convierte los roles del usuario a lista de strings
     */
    public List<String> getUserRoles(User user) {
        return user.getRoles().stream()
                .map(Role::name)
                .toList();
    }

}
