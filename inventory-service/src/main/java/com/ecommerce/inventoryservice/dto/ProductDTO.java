package com.ecommerce.inventoryservice.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para exponer datos del producto a través de la API.
 */
@Getter
@Setter
public class ProductDTO {
    private String sku;
    private String name;
    private String description;
    private boolean active;
    private Double price;
    private String category;
    private Integer quantity;
}
