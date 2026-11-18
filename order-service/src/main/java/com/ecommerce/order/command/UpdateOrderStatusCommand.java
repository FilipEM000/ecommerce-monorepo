package com.ecommerce.order.command;

import com.ecommerce.order.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusCommand(
        @NotNull OrderStatus status
) {
}
