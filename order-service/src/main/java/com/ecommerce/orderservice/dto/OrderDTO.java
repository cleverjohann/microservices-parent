package com.ecommerce.orderservice.dto;

import com.ecommerce.orderservice.models.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDTO {
    private Long id;
    private String sku;
    private Integer quantity;
    private OrderStatus status;
}
