package com.ecommerce.inventoryservice.controller;

import com.ecommerce.inventoryservice.dto.CreateProductDTO;
import com.ecommerce.inventoryservice.dto.ProductDTO;
import com.ecommerce.inventoryservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{sku}")
    public ResponseEntity<ProductDTO> getProductBySku(@PathVariable("sku") String sku) {
        log.info("Consultando producto con SKU: {}", sku); // <-- agregado
        return productService.findById(sku)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody CreateProductDTO dto){
        log.info("Creando producto: {}", dto); // <-- agregado
        return productService.saveProduct(dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts(){
        log.info("Obteniendo todos los productos"); // <-- agregado
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PutMapping("/{sku}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("sku") String sku, @RequestBody ProductDTO dto){
        log.info("Actualizando producto con SKU: {}, datos: {}", sku, dto); // <-- agregado
        return productService.updateProduct(sku, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{sku}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("sku") String sku){
        log.info("Eliminando producto con SKU: {}", sku); // <-- agregado
        productService.deleteProduct(sku);
        return ResponseEntity.ok().build();
    }
}