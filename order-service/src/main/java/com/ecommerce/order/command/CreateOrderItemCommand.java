package com.ecommerce.order.command;

import com.ecommerce.order.model.SelectedConfiguration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderItemCommand(
        @NotNull Long productId,
        @NotBlank String productName,
        @NotNull @Positive BigDecimal unitPrice,
        @NotNull @Positive Integer quantity,
        @Valid List<SelectedConfiguration> configurations
) {
}
