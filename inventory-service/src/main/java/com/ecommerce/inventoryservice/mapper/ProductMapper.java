package com.ecommerce.inventoryservice.mapper;

import com.ecommerce.inventoryservice.dto.CreateProductDTO;
import com.ecommerce.inventoryservice.dto.ProductDTO;
import com.ecommerce.inventoryservice.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

/**
 * Mapper para convertir entre entidades y DTOs de productos.
 */
@Mapper(
        componentModel = "spring", //generar un bean
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public interface ProductMapper {

    Product createProductDTO(CreateProductDTO dto);

    ProductDTO productDTO(Product product);

    Product toEntity(ProductDTO dto);

    List<ProductDTO> productDTOList(List<Product> products);
}
