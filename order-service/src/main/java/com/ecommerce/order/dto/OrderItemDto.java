package com.ecommerce.order.dto;

import com.ecommerce.order.model.SelectedConfiguration;

import java.math.BigDecimal;
import java.util.List;

public record OrderItemDto(
        Long id,
        Long productId,
        String productName,
        BigDecimal unitPrice,
        Integer quantity,
        List<SelectedConfiguration> configurations
) {
}
