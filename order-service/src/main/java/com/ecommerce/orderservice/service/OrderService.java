package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.dto.CreateOrderDTO;
import com.ecommerce.orderservice.dto.OrderDTO;
import com.ecommerce.orderservice.dto.UpdateOrderDTO;

import java.util.List;

public interface OrderService {
    OrderDTO saveOrder(CreateOrderDTO request);

    OrderDTO getOrder(Long id);

    void deleteOrder(Long id);

    OrderDTO updateOrder(Long id, UpdateOrderDTO request);

    List<OrderDTO> getAllOrders();
}
