package com.ecommerce.inventoryservice.controller;

import com.ecommerce.inventoryservice.dto.ProductDTO;
import com.ecommerce.inventoryservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false) // desactivar Spring Security para pruebas
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void getProductBySku_ReturnsProduct_WhenExists() throws Exception {
        ProductDTO product = new ProductDTO();
        product.setSku("pro1");
        product.setName("Laptop Lenovo");
        product.setDescription("Laptop de prueba");
        product.setActive(true);
        product.setPrice(3200.0);
        product.setCategory("Computers");
        product.setQuantity(5);

        when(productService.findById("pro1")).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/inventory/pro1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("pro1"))
                .andExpect(jsonPath("$.name").value("Laptop Lenovo"))
                .andExpect(jsonPath("$.price").value(3200.0))
                .andExpect(jsonPath("$.quantity").value(5));
    }

    @Test
    void getProductBySku_ReturnsNotFound_WhenNotExists() throws Exception {
        when(productService.findById("pro3")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/inventory/SKU999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
