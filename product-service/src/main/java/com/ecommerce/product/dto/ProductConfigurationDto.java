package com.ecommerce.product.dto;

import java.math.BigDecimal;

public record ProductConfigurationDto(
        Long id,
        String category,
        String value,
        BigDecimal additionalPrice,
        Boolean isDefault
) {
}
