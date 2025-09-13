package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.dto.CreateOrderDTO;
import com.ecommerce.orderservice.dto.OrderDTO;
import com.ecommerce.orderservice.mapper.OrderMapper;
import com.ecommerce.orderservice.models.Order;
import com.ecommerce.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderDTO saveOrder(CreateOrderDTO request) {
        Order ordersave = orderRepository.save(orderMapper.createOrderDTO(request));
        return orderMapper.orderDTO(ordersave);
    }

    public OrderDTO getOrder(Long id){
        return orderMapper.orderDTO(orderRepository.findById(id).orElseThrow());
    }

    public void deleteOrder(Long id){
        orderRepository.deleteById(id);
    }

    public OrderDTO updateOrder(Long id, OrderDTO request){
        Order updateOrder = orderRepository.findById(id).orElseThrow();
        updateOrder.setSku(request.getSku());
        updateOrder.setQuantity(request.getQuantity());
        return orderMapper.orderDTO(orderRepository.save(updateOrder));
    }

    public List<OrderDTO> getAllOrders(){
        return orderMapper.orderDTOList(orderRepository.findAll());
    }
}
