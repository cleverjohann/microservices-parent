
package com.ecommerce.authservice.config;

import com.ecommerce.authservice.models.Role;
import com.ecommerce.authservice.models.User;
import com.ecommerce.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@ecommerce.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin123!}")
    private String adminPassword;

    @Value("${app.admin.firstName:Admin}")
    private String adminFirstName;

    @Value("${app.admin.lastName:Sistema}")
    private String adminLastName;

    @Value("${app.data.init.enabled:true}")
    private boolean dataInitEnabled;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (!dataInitEnabled) {
            log.info("🚫 Inicialización de datos deshabilitada");
            return;
        }

        log.info("🚀 Iniciando inicialización de datos...");

        try {
            createAdminUser();
            createTestUsers();

            log.info(" Inicialización de datos completada exitosamente");
        } catch (Exception e) {
            log.error(" Error durante la inicialización de datos", e);
            throw e;
        }
    }

    private void createAdminUser() {
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            log.info("👤 Usuario administrador ya existe: {}", adminEmail);
            return;
        }

        User adminUser = User.builder()
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .firstName(adminFirstName)
                .lastName(adminLastName)
                .roles(List.of(Role.ADMIN))  // Usar List<Role> como en el modelo
                .enabled(true)
                .build();

        userRepository.save(adminUser);

        log.info(" Usuario administrador creado exitosamente:");
        log.info("    Email: {}", adminEmail);
        log.info("    Password: {} (cambiar en producción)", adminPassword);
        log.info("    Nombre: {} {}", adminFirstName, adminLastName);
        log.info("    Roles: {}", adminUser.getRoles());
    }

    private void createTestUsers() {
        createTestUser("user@test.com", "User123!", "Test", "User", Role.USER);
        createTestUser("manager@test.com", "Manager123!", "Test", "Manager", Role.ADMIN);
    }

    private void createTestUser(String email, String password, String firstName,
                                String lastName, Role role) {
        if (userRepository.findByEmail(email).isPresent()) {
            log.info("👤 Usuario de prueba ya existe: {}", email);
            return;
        }

        User testUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .roles(List.of(role))  // Usar List<Role> y el enum Role
                .enabled(true)
                .build();

        userRepository.save(testUser);
        log.info("🧪 Usuario de prueba creado: {} - {}", email, role);
    }
}