package com.ecommerce.cart.dto;

import com.ecommerce.cart.model.SelectedConfiguration;

import java.math.BigDecimal;
import java.util.List;

public record CartItemDto(
        Long id,
        Long cartId,
        Long productId,
        String productName,
        BigDecimal unitPrice,
        Integer quantity,
        List<SelectedConfiguration> configurations
) {
}
