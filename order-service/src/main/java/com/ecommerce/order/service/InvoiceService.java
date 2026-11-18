package com.ecommerce.order.service;

import com.ecommerce.order.dto.CustomerDetailsDto;
import com.ecommerce.order.dto.invoice.InvoiceDto;
import com.ecommerce.order.dto.invoice.InvoiceItemDto;
import com.ecommerce.order.dto.invoice.SellerDto;
import com.ecommerce.order.exception.CannotGenerateInvoiceException;
import com.ecommerce.order.exception.OrderNotFoundException;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvoiceService {
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public InvoiceDto generateInvoice(Long orderId) {
        log.info("Generating invoice for order ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (order.getStatus() != OrderStatus.PAID && order.getStatus() != OrderStatus.COMPLETED) {
            throw new CannotGenerateInvoiceException(
                    "Cannot generate invoice for order in status: " + order.getStatus() + ". Order must be PAID or COMPLETED.");
        }
        List<InvoiceItemDto> items = order.getItems().stream()
                .map(item -> new InvoiceItemDto(
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalItemPrice()
                ))
                .toList();
        CustomerDetailsDto buyer = new CustomerDetailsDto(
                order.getCustomerDetails().getFirstName(),
                order.getCustomerDetails().getLastName(),
                order.getCustomerDetails().getAddress(),
                order.getCustomerDetails().getEmail(),
                order.getCustomerDetails().getPhoneNumber()
        );
        String invoiceNumber = "INV/" + LocalDate.now().getYear() + "/" + order.getId();
        return new InvoiceDto(
                invoiceNumber,
                LocalDate.now(),
                SellerDto.defaultSeller(),
                buyer,
                items,
                order.getTotalAmount(),
                order.getPaymentMethod()
        );
    }
}