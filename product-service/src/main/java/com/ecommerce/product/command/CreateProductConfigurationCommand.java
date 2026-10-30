package com.ecommerce.product.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateProductConfigurationCommand(
        @NotBlank
        String category,
        @NotBlank
        String value,
        @NotNull
        BigDecimal additionalPrice,
        Boolean isDefault
) {
}
