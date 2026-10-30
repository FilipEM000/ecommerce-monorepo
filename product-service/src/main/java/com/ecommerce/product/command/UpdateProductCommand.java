package com.ecommerce.product.command;

import com.ecommerce.product.model.ProductType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;

public record UpdateProductCommand(
        @NotBlank String name,
        String description,
        @NotNull ProductType productType,
        @NotNull @Positive BigDecimal basePrice,
        @NotNull @PositiveOrZero Integer totalQuantity,
        @Valid List<CreateProductConfigurationCommand> configurations
) {
}