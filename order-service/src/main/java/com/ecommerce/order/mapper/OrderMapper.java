package com.ecommerce.order.mapper;

import com.ecommerce.order.command.CreateOrderCommand;
import com.ecommerce.order.dto.OrderDto;
import com.ecommerce.order.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    OrderDto toDto(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    Order toEntity(CreateOrderCommand createOrderCommand);
}
