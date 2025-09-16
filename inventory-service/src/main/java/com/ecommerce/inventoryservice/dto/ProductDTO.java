package com.ecommerce.inventoryservice.dto;

import lombok.Data;

/**
 * DTO para exponer datos del producto a través de la API.
 */
@Data
public class ProductDTO {

    private String name;
    private String description;
    private boolean active;
    private Double price;
    private String category;
    private Integer quantity;
}
