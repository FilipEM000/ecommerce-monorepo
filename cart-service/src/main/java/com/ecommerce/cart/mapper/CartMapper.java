package com.ecommerce.cart.mapper;

import com.ecommerce.cart.dto.CartDto;
import com.ecommerce.cart.model.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {

    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    CartDto toDto(Cart cart);

    Cart toEntity(CartDto cartDto);
}
