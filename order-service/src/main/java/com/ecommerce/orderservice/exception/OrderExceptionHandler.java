package com.ecommerce.orderservice.exception;

import com.ecommerce.sharedlib.dto.ApiResponse;
import com.ecommerce.sharedlib.exception.BaseExceptionHandler;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handler de excepciones específico para el servicio de órdenes.
 * Extiende BaseExceptionHandler para heredar el manejo común de todos los microservicios.
 */
@Slf4j
@RestControllerAdvice
public class OrderExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ApiResponse<String>> handleFeignNotFound(FeignException.NotFound ex) {
        log.warn("Producto no encontrado en inventory-service: {}", ex.getMessage());

        // Extraer el SKU del mensaje de error si es posible
        String message = "Producto no encontrado";
        if (ex.getMessage().contains("/api/inventory/")) {
            String[] parts = ex.getMessage().split("/api/inventory/");
            if (parts.length > 1) {
                String sku = parts[1].split("\\]")[0]; // Extraer el SKU
                message = "Producto no encontrado: " + sku;
            }
        }

        ApiResponse<String> response = ApiResponse.error(message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<String>> handleFeignException(FeignException ex) {
        log.error("Error comunicándose con inventory-service: {}", ex.getMessage());

        ApiResponse<String> response = ApiResponse.error(
                "Error al consultar el servicio de inventario"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

}

