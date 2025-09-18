package com.ecommerce.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderDTO {
    private Long id;

    @NotBlank(message = "El SKU no puede estar vacío")
    @Size(min = 1, max = 50, message = "El SKU debe tener entre 1 y 50 caracteres")
    private String sku;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer quantity;
}

