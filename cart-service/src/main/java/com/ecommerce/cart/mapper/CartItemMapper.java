package com.ecommerce.cart.mapper;

import com.ecommerce.cart.command.AddCartItemCommand;
import com.ecommerce.cart.dto.CartItemDto;
import com.ecommerce.cart.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    CartItemDto toDto(CartItem cartItem);

    CartItem toEntity(CartItemDto cartItemDto);

    @Mapping(target = "id", ignore = true)
    CartItem toEntity(AddCartItemCommand addCartItemCommand);
}
