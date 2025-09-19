package com.ecommerce.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Nombre es requerido")
    @Size(min = 2, max = 50, message = "Nombre debe tener entre 2 y 50 caracteres")
    private String firstName;

    @NotBlank(message = "Apellido es requerido")
    @Size(min = 2, max = 50, message = "Apellido debe tener entre 2 y 50 caracteres")
    private String lastName;

    @NotBlank(message = "Email es requerido")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "Password es requerido")
    @Size(min = 6, message = "Password debe tener al menos 6 caracteres")
    private String password;

}
