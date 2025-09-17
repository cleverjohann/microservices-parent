package com.ecommerce.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderDTO {
    private Long id;
    private String sku;
    private Integer quantity;
}
