package com.ecommerce.order.dto.invoice;

import com.ecommerce.order.dto.CustomerDetailsDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceDto(
        String invoiceNumber,
        LocalDate issueDate,
        SellerDto seller,
        CustomerDetailsDto buyer,
        List<InvoiceItemDto> items,
        BigDecimal totalAmount,
        String paymentMethod
) {
}
