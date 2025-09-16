package com.ecommerce.inventoryservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    public String description;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    @Positive
    private Double price;

    private String category;

    @Column(nullable = false)
    private Integer quantity;

}
