package com.ecommerce.order.mapper;

import com.ecommerce.order.command.CreateOrderItemCommand;
import com.ecommerce.order.dto.OrderItemDto;
import com.ecommerce.order.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemDto toDto(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    OrderItem toEntity(CreateOrderItemCommand createOrderItemCommand);
}
