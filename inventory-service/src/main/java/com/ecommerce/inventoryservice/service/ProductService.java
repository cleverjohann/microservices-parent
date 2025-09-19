package com.ecommerce.inventoryservice.service;

import com.ecommerce.inventoryservice.dto.CreateProductDTO;
import com.ecommerce.inventoryservice.dto.ProductDTO;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de servicio para la gestión de productos.
 */
public interface ProductService {

    Optional<ProductDTO> findById(String sku);
    Optional<ProductDTO> saveProduct(CreateProductDTO dto);
    List<ProductDTO> getAllProducts();
    Optional<ProductDTO> updateProduct(String sku, ProductDTO dto);
    void deleteProduct(String sku);

    Optional<ProductDTO> reserveStock(String sku, Integer quantity);
    Optional<ProductDTO> releaseStock(String sku, Integer quantity); //liberar stock



}
