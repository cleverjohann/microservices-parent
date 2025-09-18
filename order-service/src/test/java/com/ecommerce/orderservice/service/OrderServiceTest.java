package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.client.InventoryClient;
import com.ecommerce.orderservice.dto.CreateOrderDTO;
import com.ecommerce.orderservice.dto.OrderDTO;
import com.ecommerce.orderservice.dto.ProductDTO;
import com.ecommerce.orderservice.exception.OrderExceptions;
import com.ecommerce.orderservice.mapper.OrderMapper;
import com.ecommerce.orderservice.models.Order;
import com.ecommerce.orderservice.models.OrderStatus;
import com.ecommerce.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Test
    void testSaveOrder_DatosValidos_CreaOrden() {
        // Arranque
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderMapper orderMapper = mock(OrderMapper.class);
        InventoryClient inventoryClient = mock(InventoryClient.class);

        // Datos de entrada
        CreateOrderDTO request = new CreateOrderDTO();
        request.setSku("LAPTOP-001");
        request.setQuantity(2);

        // Producto simulado del inventario
        ProductDTO product = new ProductDTO();
        product.setSku("LAPTOP-001");
        product.setName("Laptop Dell");
        product.setPrice(999.99);
        product.setQuantity(10); // Stock suficiente
        product.setActive(true);

        // Orden simulada creada por el mapper
        Order nuevaOrden = new Order();
        nuevaOrden.setSku("LAPTOP-001");
        nuevaOrden.setQuantity(2);
        nuevaOrden.setStatus(OrderStatus.CREATED);

        // Orden guardada con ID
        Order ordenGuardada = new Order();
        ordenGuardada.setId(1L);
        ordenGuardada.setSku("LAPTOP-001");
        ordenGuardada.setQuantity(2);
        ordenGuardada.setStatus(OrderStatus.CREATED);

        // DTO de respuesta
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setSku("LAPTOP-001");
        orderDTO.setQuantity(2);
        orderDTO.setStatus(OrderStatus.CREATED);

        // Configurar mocks
        when(inventoryClient.getProductBySku("LAPTOP-001")).thenReturn(product);
        when(orderMapper.createOrderDTO(request)).thenReturn(nuevaOrden);
        when(orderRepository.save(any(Order.class))).thenReturn(ordenGuardada);
        when(orderMapper.orderDTO(ordenGuardada)).thenReturn(orderDTO);
        when(inventoryClient.updateProduct(eq("LAPTOP-001"), any(ProductDTO.class))).thenReturn(product);

        // Acción
        OrderService orderService = new OrderServiceImpl(orderRepository, orderMapper, inventoryClient);
        OrderDTO resultado = orderService.saveOrder(request);

        // Verificación
        Assertions.assertEquals(1L, resultado.getId());
        Assertions.assertEquals("LAPTOP-001", resultado.getSku());
        Assertions.assertEquals(2, resultado.getQuantity());
        Assertions.assertEquals(OrderStatus.CREATED, resultado.getStatus());

        // Verificar interacciones
        verify(inventoryClient).getProductBySku("LAPTOP-001");
        verify(inventoryClient).updateProduct(eq("LAPTOP-001"), any(ProductDTO.class));
        verify(orderRepository).save(any(Order.class));
        verify(orderMapper).createOrderDTO(request);
        verify(orderMapper).orderDTO(ordenGuardada);
    }

    @Test
    void testSaveOrder_ProductoNoExiste_LanzaExcepcion() {
        // Arranque
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderMapper orderMapper = mock(OrderMapper.class);
        InventoryClient inventoryClient = mock(InventoryClient.class);

        // Datos de entrada
        CreateOrderDTO request = new CreateOrderDTO();
        request.setSku("PRODUCTO-INEXISTENTE");
        request.setQuantity(1);

        // Simular que el producto no existe
        when(inventoryClient.getProductBySku("PRODUCTO-INEXISTENTE"))
                .thenThrow(new RuntimeException("Producto no encontrado"));

        // Acción y Verificación
        OrderService orderService = new OrderServiceImpl(orderRepository, orderMapper, inventoryClient);

        Assertions.assertThrows(RuntimeException.class, () -> {
            orderService.saveOrder(request);
        });

        // Verificar que no se guardó nada
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testSaveOrder_StockInsuficiente_LanzaExcepcion() {
        // Arranque
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderMapper orderMapper = mock(OrderMapper.class);
        InventoryClient inventoryClient = mock(InventoryClient.class);

        // Datos de entrada
        CreateOrderDTO request = new CreateOrderDTO();
        request.setSku("LAPTOP-001");
        request.setQuantity(15); // Más de lo disponible

        // Producto con stock insuficiente
        ProductDTO product = new ProductDTO();
        product.setSku("LAPTOP-001");
        product.setQuantity(5); // Solo 5 disponibles
        product.setActive(true);

        when(inventoryClient.getProductBySku("LAPTOP-001")).thenReturn(product);

        // Acción y Verificación
        OrderService orderService = new OrderServiceImpl(orderRepository, orderMapper, inventoryClient);

        Assertions.assertThrows(OrderExceptions.InsufficientStockException.class, () -> {
            orderService.saveOrder(request);
        });

        // Verificar que no se guardó la orden
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testGetOrder_OrdenExiste_RetornaOrden() {
        // Arranque
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderMapper orderMapper = mock(OrderMapper.class);
        InventoryClient inventoryClient = mock(InventoryClient.class);

        Long id = 1L;
        Order order = new Order();
        order.setId(id);
        order.setSku("LAPTOP-001");
        order.setQuantity(2);
        order.setStatus(OrderStatus.CREATED);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(id);
        orderDTO.setSku("LAPTOP-001");
        orderDTO.setQuantity(2);
        orderDTO.setStatus(OrderStatus.CREATED);

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderMapper.orderDTO(order)).thenReturn(orderDTO);

        // Acción
        OrderService orderService = new OrderServiceImpl(orderRepository, orderMapper, inventoryClient);
        OrderDTO resultado = orderService.getOrder(id);

        // Verificación
        Assertions.assertEquals(id, resultado.getId());
        Assertions.assertEquals("LAPTOP-001", resultado.getSku());
        Assertions.assertEquals(2, resultado.getQuantity());
        Assertions.assertEquals(OrderStatus.CREATED, resultado.getStatus());

        verify(orderRepository).findById(id);
        verify(orderMapper).orderDTO(order);
    }

    @Test
    void testGetOrder_OrdenNoExiste_LanzaExcepcion() {
        // Arranque
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderMapper orderMapper = mock(OrderMapper.class);
        InventoryClient inventoryClient = mock(InventoryClient.class);

        Long id = 999L;
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        // Acción y Verificación
        OrderService orderService = new OrderServiceImpl(orderRepository, orderMapper, inventoryClient);

        Assertions.assertThrows(OrderExceptions.OrderNotFoundException.class, () -> {
            orderService.getOrder(id);
        });

        verify(orderRepository).findById(id);
        verify(orderMapper, never()).orderDTO(any());
    }

    @Test
    void testDeleteOrder_OrdenExiste_CancelaOrden() {
        // Arranque
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderMapper orderMapper = mock(OrderMapper.class);
        InventoryClient inventoryClient = mock(InventoryClient.class);

        Long id = 1L;
        Order order = new Order();
        order.setId(id);
        order.setSku("LAPTOP-001");
        order.setQuantity(2);
        order.setStatus(OrderStatus.CREATED);

        // Producto para restaurar stock
        ProductDTO product = new ProductDTO();
        product.setSku("LAPTOP-001");
        product.setQuantity(8); // Stock actual
        product.setActive(true);

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(inventoryClient.getProductBySku("LAPTOP-001")).thenReturn(product);
        when(inventoryClient.updateProduct(eq("LAPTOP-001"), any(ProductDTO.class))).thenReturn(product);

        // Acción
        OrderService orderService = new OrderServiceImpl(orderRepository, orderMapper, inventoryClient);
        orderService.deleteOrder(id);

        // Verificación
        verify(orderRepository).findById(id);
        verify(inventoryClient).getProductBySku("LAPTOP-001");
        verify(inventoryClient).updateProduct(eq("LAPTOP-001"), any(ProductDTO.class));
        verify(orderRepository).save(any(Order.class));
    }
}