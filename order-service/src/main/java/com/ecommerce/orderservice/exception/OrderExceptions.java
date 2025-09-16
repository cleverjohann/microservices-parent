package com.ecommerce.orderservice.exception;

import com.ecommerce.sharedlib.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Excepciones específicas del dominio de órdenes
 */
public class OrderExceptions {

    public static class OrderNotFoundException extends BusinessException {
        public OrderNotFoundException(Long id) {
            super("Orden no encontrada: " + id, HttpStatus.NOT_FOUND);
        }
    }

    public static class InsufficientStockException extends BusinessException {
        public InsufficientStockException(int available, int requested) {
            super(String.format("Stock insuficiente. Disponible: %d, Solicitado: %d",
                    available, requested), HttpStatus.CONFLICT);
        }
    }

    public static class ProductNotFoundException extends BusinessException {
        public ProductNotFoundException(String sku) {
            super("Producto no encontrado: " + sku, HttpStatus.NOT_FOUND);
        }
    }

    public static class ProductNotAvailableException extends BusinessException {
        public ProductNotAvailableException(String sku) {
            super("Producto no disponible: " + sku, HttpStatus.CONFLICT);
        }
    }
}

