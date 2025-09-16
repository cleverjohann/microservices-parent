package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.client.InventoryClient;
import com.ecommerce.orderservice.dto.CreateOrderDTO;
import com.ecommerce.orderservice.dto.OrderDTO;
import com.ecommerce.orderservice.dto.ProductDTO;
import com.ecommerce.orderservice.dto.UpdateOrderDTO;
import com.ecommerce.orderservice.exception.OrderExceptions;
import com.ecommerce.orderservice.mapper.OrderMapper;
import com.ecommerce.orderservice.models.Order;
import com.ecommerce.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final InventoryClient inventoryClient;


    @Transactional
    public OrderDTO saveOrder(CreateOrderDTO request) {
        // 1. Obtener y validar producto
        ProductDTO product = inventoryClient.getProductBySku(request.getSku());
        validateProduct(product, request);

        // 2. Actualizar stock
        product.setQuantity(product.getQuantity() - request.getQuantity());
        inventoryClient.updateProduct(request.getSku(), product);

        // 3. Crear orden
        Order orderSave = orderRepository.save(orderMapper.createOrderDTO(request));

        log.info("Orden creada exitosamente. ID: {}, SKU: {}, Cantidad: {}",
                orderSave.getId(), request.getSku(), request.getQuantity());

        return orderMapper.orderDTO(orderSave);
    }


    public OrderDTO getOrder(Long id){
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderExceptions.OrderNotFoundException(id));
        return orderMapper.orderDTO(order);

    }

    public void deleteOrder(Long id){
        if (!orderRepository.existsById(id)) {
            throw new OrderExceptions.OrderNotFoundException(id);
        }
        orderRepository.deleteById(id);
    }

    @Transactional
    public OrderDTO updateOrder(Long id, UpdateOrderDTO request) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new OrderExceptions.OrderNotFoundException(id));

        // Usar el mapper para actualizar solo los campos permitidos
        orderMapper.updateOrderFromDTO(request, existingOrder);

        Order updatedOrder = orderRepository.save(existingOrder);

        log.info("Orden actualizada exitosamente. ID: {}, SKU: {}, Cantidad: {}",
                updatedOrder.getId(), updatedOrder.getSku(), updatedOrder.getQuantity());

        return orderMapper.orderDTO(updatedOrder);
    }


    public List<OrderDTO> getAllOrders(){
        return orderMapper.orderDTOList(orderRepository.findAll());
    }

    private void validateProduct(ProductDTO product, CreateOrderDTO request) {
        if (product == null) {
            throw new OrderExceptions.ProductNotFoundException(request.getSku());
        }

        if (!product.isActive()) {
            throw new OrderExceptions.ProductNotAvailableException(request.getSku());
        }

        if (product.getQuantity() < request.getQuantity()) {
            throw new OrderExceptions.InsufficientStockException(
                    product.getQuantity(), request.getQuantity());
        }
    }

}
