package com.ecommerce.product.dto;

import com.ecommerce.product.model.ProductType;

import java.math.BigDecimal;
import java.util.List;

public record ProductDto(
        Long id,
        String name,
        String description,
        ProductType productType,
        BigDecimal basePrice,
        Integer totalQuantity,
        List<ProductConfigurationDto> configurations
) {
}
