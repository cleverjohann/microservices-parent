package com.ecommerce.inventoryservice.controller;

import com.ecommerce.inventoryservice.dto.CreateProductDTO;
import com.ecommerce.inventoryservice.dto.ProductDTO;
import com.ecommerce.inventoryservice.service.ProductService;
import com.ecommerce.sharedlib.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j //agregar logger
@RestController // devuelven JSON y es controlador
@RequestMapping("/api/inventory")
@RequiredArgsConstructor //
public class ProductController {

    @Autowired
    private MessageSource messageSource;

    private final ProductService productService;

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    @GetMapping("/{sku}")
    public ResponseEntity<ProductDTO> getProductBySku(@PathVariable("sku") String sku) {
        log.info("Consultando producto con SKU: {}", sku);
        return productService.findById(sku)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody @Valid CreateProductDTO dto){
        log.info("Creando producto: {}", dto);
        return productService.saveProduct(dto)
                .map(product -> ResponseEntity.status(HttpStatus.CREATED).body(product))
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        log.info("Obteniendo todos los productos");
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{sku}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable("sku") String sku,
            @RequestBody ProductDTO dto) {

        log.info("Actualizando producto con SKU: {}, datos: {}", sku, dto);

        return productService.updateProduct(sku, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{sku}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("sku") String sku){
        log.info("Eliminando producto con SKU: {}", sku);
        productService.deleteProduct(sku);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{sku}/reserve")
    public ResponseEntity<ApiResponse<ProductDTO>> reserveStock(
            @PathVariable("sku") String sku,
            @RequestParam("quantity") Integer quantity) {

        return productService.reserveStock(sku, quantity)
                .map(updated -> ResponseEntity.ok(
                        ApiResponse.success(updated, getMessage("stock.reserve.success"))
                ))
                .orElse(ResponseEntity.badRequest()
                        .body(ApiResponse.error(getMessage("stock.reserve.error"))));
    }

    @PostMapping("/{sku}/release")
    public ResponseEntity<ApiResponse<ProductDTO>> releaseStock(
            @PathVariable("sku") String sku,
            @RequestParam("quantity")  Integer quantity) {
        return productService.releaseStock(sku, quantity)
                .map(updated -> ResponseEntity.ok(ApiResponse.success(updated, "Stock liberado con éxito")))
                .orElse(ResponseEntity.badRequest().body(ApiResponse.error("No se pudo liberar stock")));
    }

}