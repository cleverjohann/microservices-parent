package com.ecommerce.orderservice.dto;

import lombok.Data;

@Data
public class CreateOrderDTO {
    private Long id;
    private String sku;
    private Integer quantity;
}
