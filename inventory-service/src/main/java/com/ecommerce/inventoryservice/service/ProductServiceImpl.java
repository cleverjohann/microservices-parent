package com.ecommerce.inventoryservice.service;

import com.ecommerce.inventoryservice.dto.CreateProductDTO;
import com.ecommerce.inventoryservice.dto.ProductDTO;
import com.ecommerce.inventoryservice.mapper.ProductMapper;
import com.ecommerce.inventoryservice.models.Product;
import com.ecommerce.inventoryservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de gestión de productos.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Optional<ProductDTO> findById(String sku) {
        return productRepository.findById(sku).map(productMapper::productDTO);
    }

    @Override
    public Optional<ProductDTO> saveProduct(CreateProductDTO dto) {
        Product saved = productRepository.save(productMapper.createProductDTO(dto));
        return Optional.of(productMapper.productDTO(saved));
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productMapper.productDTOList(productRepository.findAll());
    }

    @Override
    public Optional<ProductDTO> updateProduct(String sku, ProductDTO dto) {
        return productRepository.findById(sku).map(existing -> {
            existing.setName(dto.getName());
            existing.setDescription(dto.getDescription());
            existing.setActive(dto.isActive());
            existing.setPrice(dto.getPrice());
            existing.setCategory(dto.getCategory());
            existing.setQuantity(dto.getQuantity());
            return productMapper.productDTO(productRepository.save(existing));
        });
    }

    @Override
    public void deleteProduct(String sku) {
        productRepository.deleteById(sku);
    }

    @Override
    public Optional<ProductDTO> reserveStock(String sku, Integer quantity) {
        return productRepository.findById(sku).map(product -> {
            if (!product.isActive()) {
                log.warn("No se puede reservar stock. Producto {} inactivo.", sku);
                return null;
            }
            if (product.getQuantity() < quantity) {
                log.warn("Stock insuficiente para el producto {}. Solicitado: {}, Disponible: {}",
                        sku, quantity, product.getQuantity());
                return null;
            }
            product.setQuantity(product.getQuantity() - quantity);
            Product saved = productRepository.save(product);
            return productMapper.productDTO(saved);
        });
    }

    // 🔹 Nuevo: liberar stock
    @Override
    public Optional<ProductDTO> releaseStock(String sku, Integer quantity) {
        return productRepository.findById(sku).map(product -> {
            if (!product.isActive()) {
                log.warn("No se puede liberar stock. Producto {} inactivo.", sku);
                return null;
            }
            product.setQuantity(product.getQuantity() + quantity);
            Product saved = productRepository.save(product);
            return productMapper.productDTO(saved);
        });
    }
}
