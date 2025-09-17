package com.ecommerce.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

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