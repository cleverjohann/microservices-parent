package com.ecommerce.inventoryservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO utilizado para la creación de productos.
 */
@Data
public class CreateProductDTO {
    @NotBlank(message = "El SKU es obligatorio")
    private String sku;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;

    private boolean active;

    @NotNull(message = "El precio no puede ser nulo")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double price;

    private String category;

    @NotNull(message = "La cantidad no puede ser nula")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer quantity;
}