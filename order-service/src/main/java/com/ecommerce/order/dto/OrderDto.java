package com.ecommerce.order.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderDto(
        Long id,
        String status,
        String paymentMethod,
        String deliveryMethod,
        CustomerDetailsDto customerDetails,
        BigDecimal totalAmount,
        List<OrderItemDto> items
) {
}
