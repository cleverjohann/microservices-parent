package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.client.InventoryClient;
import com.ecommerce.orderservice.dto.CreateOrderDTO;
import com.ecommerce.orderservice.dto.OrderDTO;
import com.ecommerce.orderservice.dto.ProductDTO;
import com.ecommerce.orderservice.dto.UpdateOrderDTO;
import com.ecommerce.orderservice.exception.OrderExceptions;
import com.ecommerce.orderservice.mapper.OrderMapper;
import com.ecommerce.orderservice.models.Order;
import com.ecommerce.orderservice.models.OrderStatus;
import com.ecommerce.orderservice.repository.OrderRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService  {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final InventoryClient inventoryClient;

    @Override
    @Transactional
    public OrderDTO saveOrder(CreateOrderDTO request) {
        log.info("Iniciando creación de orden para SKU: {} con cantidad: {}",
                request.getSku(), request.getQuantity());

        // 1. Obtener producto del inventario
        ProductDTO product = obtenerProducto(request.getSku());

        // 2. Validar disponibilidad y stock
        validarDisponibilidadProducto(product, request);

        // 3. Reservar stock (actualizar cantidad)
        reservarStock(product, request.getQuantity());

        // 4. Crear la orden con estado CREATED automáticamente
        Order nuevaOrden = crearNuevaOrden(request);

        log.info("Orden creada exitosamente. ID: {}, SKU: {}, Cantidad: {}, Estado: {}",
                nuevaOrden.getId(), request.getSku(), request.getQuantity(), nuevaOrden.getStatus());

        return orderMapper.orderDTO(nuevaOrden);
    }

    @Override
    public OrderDTO getOrder(Long id) {
        log.debug("Buscando orden con ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderExceptions.OrderNotFoundException(id));

        return orderMapper.orderDTO(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        log.info("Cancelando orden con ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderExceptions.OrderNotFoundException(id));

        // Verificar que la orden no esté ya cancelada
        if (order.getStatus() == OrderStatus.CANCELED) {
            log.warn("La orden {} ya está cancelada", id);
            return;
        }

        // 1. Restaurar el stock al inventario antes de cancelar
        restaurarStock(order.getSku(), order.getQuantity());

        // 2. Cambiar estado a CANCELED en lugar de eliminar físicamente
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        log.info("Orden {} cancelada exitosamente y stock restaurado", id);
    }

    @Override
    @Transactional
    public OrderDTO updateOrder(Long id, UpdateOrderDTO request) {
        log.info("Actualizando cantidad de orden ID: {} - Nueva cantidad: {}",
                id, request.getQuantity());

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new OrderExceptions.OrderNotFoundException(id));

        // Verificar que la orden no esté cancelada
        if (existingOrder.getStatus() == OrderStatus.CANCELED) {
            throw new OrderExceptions.OrderCanceledException(id);
        }

        // Guardar valores actuales
        String sku = existingOrder.getSku();
        Integer cantidadAnterior = existingOrder.getQuantity();
        Integer nuevaCantidad = request.getQuantity();

        // Si la cantidad es la misma, no hacer nada
        if (cantidadAnterior.equals(nuevaCantidad)) {
            log.info("La cantidad no ha cambiado para la orden {}", id);
            return orderMapper.orderDTO(existingOrder);
        }

        // Obtener el producto actual para validar stock disponible
        ProductDTO producto = obtenerProducto(sku);

        // Validar que el producto siga activo
        if (!producto.isActive()) {
            throw new OrderExceptions.ProductNotAvailableException(sku);
        }

        // Calcular diferencia de stock
        Integer diferencia = nuevaCantidad - cantidadAnterior;

        if (diferencia > 0) {
            // Necesitamos más stock - validar disponibilidad
            if (producto.getQuantity() < diferencia) {
                throw new OrderExceptions.InsufficientStockException(
                        producto.getQuantity(), diferencia);
            }

            // Reservar stock adicional
            reservarStock(producto, diferencia);
            log.info("Stock adicional reservado. SKU: {}, Cantidad adicional: {}", sku, diferencia);

        } else {
            // Liberamos stock (diferencia es negativa)
            Integer stockALiberar = Math.abs(diferencia);
            restaurarStock(sku, stockALiberar);
            log.info("Stock liberado. SKU: {}, Cantidad liberada: {}", sku, stockALiberar);
        }

        // Actualizar solo la cantidad en la orden
        existingOrder.setQuantity(nuevaCantidad);
        Order updatedOrder = orderRepository.save(existingOrder);

        log.info("Orden actualizada exitosamente. ID: {}, SKU: {}, Cantidad: {} -> {}",
                id, sku, cantidadAnterior, nuevaCantidad);

        return orderMapper.orderDTO(updatedOrder);
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        log.debug("Obteniendo todas las órdenes");

        List<Order> orders = orderRepository.findAll();
        return orderMapper.orderDTOList(orders);
    }


    private ProductDTO obtenerProducto(String sku) {
        try {
            return inventoryClient.getProductBySku(sku);

        } catch (FeignException.NotFound ex) {
            // El OrderExceptionHandler se encarga de manejar FeignException.NotFound
            // y convertirla en una respuesta adecuada
            throw ex;

        } catch (FeignException ex) {
            // El OrderExceptionHandler maneja FeignException para errores de comunicación
            log.error("Error de comunicación con inventory-service para SKU {}: {}", sku, ex.getMessage());
            throw ex;

        } catch (Exception ex) {
            log.error("Error inesperado al obtener producto con SKU {}: {}", sku, ex.getMessage());
            throw new OrderExceptions.ProductNotFoundException(sku);
        }
    }

    private void validarDisponibilidadProducto(ProductDTO product, CreateOrderDTO request) {
        // Verificar que el producto no sea null (validación adicional)
        if (product == null) {
            throw new OrderExceptions.ProductNotFoundException(request.getSku());
        }

        // Verificar que el producto esté activo
        if (!product.isActive()) {
            throw new OrderExceptions.ProductNotAvailableException(request.getSku());
        }

        // Verificar stock suficiente
        if (product.getQuantity() < request.getQuantity()) {
            throw new OrderExceptions.InsufficientStockException(
                    product.getQuantity(), request.getQuantity());
        }
    }

    private void reservarStock(ProductDTO product, Integer cantidad) {
        try {
            // Calcular nueva cantidad después de la reserva
            Integer nuevaCantidad = product.getQuantity() - cantidad;
            product.setQuantity(nuevaCantidad);

            // Actualizar el producto en el inventario
            ProductDTO productoActualizado = inventoryClient.updateProduct(product.getSku(), product);

            if (productoActualizado == null) {
                throw new OrderExceptions.ProductNotAvailableException(product.getSku());
            }

            log.debug("Stock reservado exitosamente. SKU: {}, Cantidad reservada: {}, Stock restante: {}",
                    product.getSku(), cantidad, nuevaCantidad);

        } catch (FeignException ex) {
            log.error("Error al reservar stock para SKU {}: {}", product.getSku(), ex.getMessage());
            throw ex;
        } catch (OrderExceptions.ProductNotAvailableException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error inesperado al reservar stock para SKU {}: {}", product.getSku(), ex.getMessage());
            throw new OrderExceptions.ProductNotAvailableException(product.getSku());
        }
    }


    private void restaurarStock(String sku, Integer cantidad) {
        try {
            // Obtener el producto actual
            ProductDTO product = obtenerProducto(sku);

            // Sumar la cantidad a restaurar
            Integer nuevaCantidad = product.getQuantity() + cantidad;
            product.setQuantity(nuevaCantidad);

            // Actualizar el producto en el inventario
            ProductDTO productoActualizado = inventoryClient.updateProduct(sku, product);

            if (productoActualizado == null) {
                log.error("Error al restaurar stock para SKU: {}", sku);
                throw new RuntimeException("Error al restaurar stock para el producto " + sku);
            }

            log.info("Stock restaurado exitosamente. SKU: {}, Cantidad restaurada: {}, Stock actual: {}",
                    sku, cantidad, nuevaCantidad);

        } catch (FeignException ex) {
            log.error("Error al restaurar stock para SKU {}: {}", sku, ex.getMessage());
            // En caso de error al restaurar, lanzamos una excepción para que la transacción haga rollback
            throw new RuntimeException("Error al restaurar stock del producto " + sku + ": " + ex.getMessage());
        } catch (Exception ex) {
            log.error("Error inesperado al restaurar stock para SKU {}: {}", sku, ex.getMessage());
            throw new RuntimeException("Error al restaurar stock del producto " + sku + ": " + ex.getMessage());
        }
    }


    private Order crearNuevaOrden(CreateOrderDTO request) {
        // Crear orden usando el mapper
        Order nuevaOrden = orderMapper.createOrderDTO(request);

        // Establecer estado CREATED automáticamente
        nuevaOrden.setStatus(OrderStatus.CREATED);

        return orderRepository.save(nuevaOrden);
    }
}
