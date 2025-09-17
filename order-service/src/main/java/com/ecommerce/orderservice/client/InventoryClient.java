package com.ecommerce.orderservice.client;

import com.ecommerce.orderservice.dto.ProductDTO;
import com.ecommerce.sharedlib.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service", path = "/api/inventory")
public interface InventoryClient {
    @GetMapping("/{sku}")
    ProductDTO getProductBySku(@PathVariable("sku") String sku);

    @PutMapping("/{sku}")
    ProductDTO updateProduct(@PathVariable("sku") String sku, @RequestBody ProductDTO product);
}

