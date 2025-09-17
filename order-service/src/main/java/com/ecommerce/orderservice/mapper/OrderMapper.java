package com.ecommerce.orderservice.mapper;

import com.ecommerce.orderservice.dto.CreateOrderDTO;
import com.ecommerce.orderservice.dto.OrderDTO;
import com.ecommerce.orderservice.dto.UpdateOrderDTO;
import com.ecommerce.orderservice.models.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public interface OrderMapper {

    Order createOrderDTO(CreateOrderDTO order);

    OrderDTO orderDTO(Order order);

    Order toEntity(OrderDTO orderDTO);

    List<OrderDTO> orderDTOList(List<Order> orders);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateOrderFromDTO(UpdateOrderDTO updateOrderDTO, @MappingTarget Order order);
}
