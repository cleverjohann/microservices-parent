package com.ecommerce.inventoryservice.dto;

import lombok.Data;

/**
 * DTO utilizado para la creación de productos.
 */
@Data
public class CreateProductDTO {
    private String sku;
    private String name;
    private String description;
    private boolean active;
    private Double price;
    private String category;
    private Integer quantity;
}
