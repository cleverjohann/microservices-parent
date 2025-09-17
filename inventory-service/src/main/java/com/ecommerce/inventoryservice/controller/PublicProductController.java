package com.ecommerce.inventoryservice.controller;

import com.ecommerce.inventoryservice.dto.CreateProductDTO;
import com.ecommerce.inventoryservice.dto.ProductDTO;
import com.ecommerce.inventoryservice.service.ProductService;
import com.ecommerce.sharedlib.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/inventory/public")
@RequiredArgsConstructor //
public class PublicProductController {

    private final ProductService productService;

    @GetMapping("/{sku}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductBySku(@PathVariable("sku") String sku) {
        log.info("Consultando producto con SKU: {}", sku);
        return productService.findById(sku)
                .map(product -> ResponseEntity.ok(ApiResponse.success(product)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Producto no encontrado"))); //respuestas consistentes
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@RequestBody @Valid CreateProductDTO dto){
        log.info("Creando producto: {}", dto);
        return productService.saveProduct(dto)
                .map(product -> ResponseEntity.ok(ApiResponse.success(product, "Producto creado con éxito")))
                .orElse(ResponseEntity.badRequest().body(ApiResponse.error("No se pudo crear el producto")));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts() {
        log.info("Obteniendo todos los productos");
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(products, "Lista de productos obtenida con éxito"));
    }

    @PutMapping("/{sku}")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(
            @PathVariable("sku") String sku,
            @RequestBody ProductDTO dto) {

        log.info("Actualizando producto con SKU: {}, datos: {}", sku, dto);

        return productService.updateProduct(sku, dto)
                .map(product -> ResponseEntity.ok(
                        ApiResponse.success(product, "Producto actualizado con éxito")))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Producto no encontrado")));
    }

    @DeleteMapping("/{sku}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("sku") String sku){
        log.info("Eliminando producto con SKU: {}", sku);
        productService.deleteProduct(sku);
        return ResponseEntity.ok().build();
    }
}
