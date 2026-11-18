package com.ecommerce.order.dto.invoice;

import java.math.BigDecimal;

public record InvoiceItemDto(
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {
}
