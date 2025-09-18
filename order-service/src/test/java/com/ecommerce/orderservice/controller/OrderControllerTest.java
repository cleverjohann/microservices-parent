package com.ecommerce.orderservice.controller;

import com.ecommerce.orderservice.dto.CreateOrderDTO;
import com.ecommerce.orderservice.dto.OrderDTO;
import com.ecommerce.orderservice.dto.UpdateOrderDTO;
import com.ecommerce.orderservice.exception.OrderExceptionHandler;
import com.ecommerce.orderservice.exception.OrderExceptions;
import com.ecommerce.orderservice.models.OrderStatus;
import com.ecommerce.orderservice.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(new OrderExceptionHandler()) // AGREGAR EL MANEJADOR DE EXCEPCIONES
                .build();
    }


    @Test
    void getOrder_OrdenExiste_Retorna200() throws Exception {
        // Arranque
        Long id = 1L;
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(id);
        orderDTO.setSku("LAPTOP-001");
        orderDTO.setQuantity(2);
        orderDTO.setStatus(OrderStatus.CREATED);

        // Cuando
        when(orderService.getOrder(id)).thenReturn(orderDTO);

        // Asegurar
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/order/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.sku").value("LAPTOP-001"))
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    void guardarOrden_DatosValidos_Retorna200() throws Exception {
        // Arranque
        CreateOrderDTO request = new CreateOrderDTO();
        request.setSku("LAPTOP-001");
        request.setQuantity(2);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setSku("LAPTOP-001");
        orderDTO.setQuantity(2);
        orderDTO.setStatus(OrderStatus.CREATED);

        // Cuando - Usar any() para evitar problemas de matching de objetos
        when(orderService.saveOrder(any(CreateOrderDTO.class))).thenReturn(orderDTO);

        // Asegurar
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"LAPTOP-001\",\"quantity\":2}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.sku").value("LAPTOP-001"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.status").value("CREATED")); // AGREGAR VALIDACIÓN DEL ESTADO

        // Verificar que el servicio fue llamado correctamente
        verify(orderService, times(1)).saveOrder(any(CreateOrderDTO.class));
    }

    @Test
    void eliminarOrden_OrdenExiste_Retorna200() throws Exception {
        // Arranque
        Long id = 1L;

        // Cuando
        doNothing().when(orderService).deleteOrder(id);

        // Asegurar
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/order/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarOrden_DatosValidos_Retorna200() throws Exception {
        // Arranque
        Long id = 1L;
        UpdateOrderDTO request = new UpdateOrderDTO();
        request.setQuantity(3);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(id);
        orderDTO.setSku("LAPTOP-001");
        orderDTO.setQuantity(3);
        orderDTO.setStatus(OrderStatus.CREATED);

        // Cuando - Usar any() para UpdateOrderDTO también
        when(orderService.updateOrder(eq(id), any(UpdateOrderDTO.class))).thenReturn(orderDTO);

        // Asegurar
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/order/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":3}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.quantity").value(3));
    }

    @Test
    void getAllOrders_OrdenesExisten_Retorna200() throws Exception {
        // Arranque
        OrderDTO order1 = new OrderDTO();
        order1.setId(1L);
        order1.setSku("LAPTOP-001");
        order1.setQuantity(2);
        order1.setStatus(OrderStatus.CREATED);

        OrderDTO order2 = new OrderDTO();
        order2.setId(2L);
        order2.setSku("MOUSE-001");
        order2.setQuantity(1);
        order2.setStatus(OrderStatus.CANCELED);

        List<OrderDTO> orders = Arrays.asList(order1, order2);

        // Cuando
        when(orderService.getAllOrders()).thenReturn(orders);

        // Asegurar
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/order")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].sku").value("LAPTOP-001"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].sku").value("MOUSE-001"));
    }

    @Test
    void guardarOrden_DatosInvalidos_Retorna400() throws Exception {
        // No mockear el servicio para este test - queremos que la validación falle antes

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"\",\"quantity\":-1}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists()); // Verificar que hay mensaje de error

        // Verificar que el servicio NO fue llamado debido a la validación
        verify(orderService, never()).saveOrder(any(CreateOrderDTO.class));
    }



    @Test
    void guardarOrden_ProductoNoExiste_Retorna404() throws Exception {
        // Simular que el producto no existe
        when(orderService.saveOrder(any(CreateOrderDTO.class)))
                .thenThrow(new OrderExceptions.ProductNotFoundException("Producto no encontrado"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"PRODUCTO-INEXISTENTE\",\"quantity\":1}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists()); // Verificar estructura de respuesta
    }


//    @Test
//    void guardarOrden_StockInsuficiente_Retorna409() throws Exception {
//        // Simular stock insuficiente
//        when(orderService.saveOrder(any(CreateOrderDTO.class)))
//                .thenThrow(new OrderExceptions.InsufficientStockException("Stock insuficiente"));
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/order")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"sku\":\"LAPTOP-001\",\"quantity\":100}")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isConflict());
//    }
}