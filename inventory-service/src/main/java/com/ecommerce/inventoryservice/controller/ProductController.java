package com.ecommerce.inventoryservice.controller;

import com.ecommerce.inventoryservice.dto.CreateProductDTO;
import com.ecommerce.inventoryservice.dto.ProductDTO;
import com.ecommerce.inventoryservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de productos.
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{sku}")
    public ResponseEntity<ProductDTO> getProductBySku(@PathVariable("sku") String sku) {
        return productService.findById(sku)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody CreateProductDTO dto){
        return productService.saveProduct(dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PutMapping("/{sku}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("sku") String sku, @RequestBody ProductDTO dto){
        return productService.updateProduct(sku, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{sku}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("sku") String sku){
        productService.deleteProduct(sku);
        return ResponseEntity.ok().build();
    }
}
