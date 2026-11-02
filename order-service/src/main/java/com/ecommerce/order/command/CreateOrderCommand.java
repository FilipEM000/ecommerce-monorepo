package com.ecommerce.order.command;

import com.ecommerce.order.model.CustomerDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderCommand(
        @NotBlank String paymentMethod,
        @NotBlank String deliveryMethod,
        @NotNull @Valid CustomerDetails customerDetails,
        @NotNull @Valid List<CreateOrderItemCommand> items
) {
}
