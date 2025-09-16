package com.ecommerce.inventoryservice.repository;

import com.ecommerce.inventoryservice.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}