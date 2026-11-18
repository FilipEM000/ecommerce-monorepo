package com.ecommerce.order.service;

import com.ecommerce.order.dto.invoice.InvoiceDto;
import com.ecommerce.order.exception.CannotGenerateInvoiceException;
import com.ecommerce.order.model.*;
import com.ecommerce.order.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

public class InvoiceServiceTest {

    private OrderRepository orderRepository;
    private InvoiceService invoiceService;

    @BeforeEach
    void setup() {
        this.orderRepository = Mockito.mock(OrderRepository.class);
        this.invoiceService = new InvoiceService(orderRepository);
    }

    @Test
    void generateInvoice_paidOrder_invoiceGenerated() {
        // GIVEN
        Long orderId = 1L;
        CustomerDetails customerDetails = new CustomerDetails(
                "test_first_name",
                "test_last_name",
                "test_address",
                "test_email@example.com",
                "123456789"
        );
        SelectedConfiguration config = new SelectedConfiguration(
                "test_category",
                "test_value",
                new BigDecimal("100.00")
        );
        OrderItem item = new OrderItem(
                100L,
                10L,
                "test_product_name",
                new BigDecimal("1000.00"),
                2,
                List.of(config)
        );
        Order order = new Order(
                orderId,
                OrderStatus.PAID,
                "test_payment_method",
                "test_delivery_method",
                customerDetails,
                new BigDecimal("2200.00"),
                List.of(item)
        );
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // WHEN
        InvoiceDto result = invoiceService.generateInvoice(orderId);

        // THEN
        String expectedInvoiceNumber = "INV/" + LocalDate.now().getYear() + "/" + orderId;
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(expectedInvoiceNumber, result.invoiceNumber()),
                () -> Assertions.assertEquals(LocalDate.now(), result.issueDate()),
                () -> Assertions.assertEquals("test_payment_method", result.paymentMethod()),
                () -> Assertions.assertEquals(new BigDecimal("2200.00"), result.totalAmount()),
                () -> Assertions.assertNotNull(result.seller()),
                () -> Assertions.assertEquals("E-Commerce", result.seller().companyName()),
                () -> Assertions.assertNotNull(result.buyer()),
                () -> Assertions.assertEquals("test_first_name", result.buyer().firstName()),
                () -> Assertions.assertEquals("test_last_name", result.buyer().lastName()),
                () -> Assertions.assertEquals("test_address", result.buyer().address()),
                () -> Assertions.assertEquals("test_email@example.com", result.buyer().email()),
                () -> Assertions.assertEquals("123456789", result.buyer().phoneNumber()),
                () -> Assertions.assertNotNull(result.items()),
                () -> Assertions.assertEquals(1, result.items().size()),
                () -> Assertions.assertEquals("test_product_name", result.items().getFirst().productName()),
                () -> Assertions.assertEquals(2, result.items().getFirst().quantity()),
                () -> Assertions.assertEquals(new BigDecimal("1000.00"), result.items().getFirst().unitPrice()),
                () -> Assertions.assertEquals(new BigDecimal("2200.00"), result.items().getFirst().totalPrice())
        );
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void generateInvoice_unpaidOrder_cannotGenerateInvoiceExceptionThrown() {
        // GIVEN
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.CREATED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // WHEN & THEN
        assertThatExceptionOfType(CannotGenerateInvoiceException.class)
                .isThrownBy(() -> invoiceService.generateInvoice(orderId));
        verify(orderRepository, times(1)).findById(orderId);
    }
}
