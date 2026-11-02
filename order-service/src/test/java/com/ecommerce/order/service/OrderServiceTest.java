package com.ecommerce.order.service;

import com.ecommerce.order.command.CreateOrderCommand;
import com.ecommerce.order.command.CreateOrderItemCommand;
import com.ecommerce.order.command.GetPageCommand;
import com.ecommerce.order.dto.OrderDto;
import com.ecommerce.order.dto.PageDto;
import com.ecommerce.order.exception.OrderNotFoundException;
import com.ecommerce.order.mapper.OrderItemMapper;
import com.ecommerce.order.mapper.OrderMapper;
import com.ecommerce.order.model.*;
import com.ecommerce.order.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    private OrderRepository orderRepository;
    private OrderMapper orderMapper;
    private OrderItemMapper orderItemMapper;
    private OrderService orderService;

    @BeforeEach
    void setup() {
        this.orderRepository = Mockito.mock(OrderRepository.class);
        this.orderItemMapper = Mappers.getMapper(OrderItemMapper.class);
        this.orderMapper = Mappers.getMapper(OrderMapper.class);
        ReflectionTestUtils.setField(orderMapper, "orderItemMapper", orderItemMapper);
        this.orderService = new OrderService(orderRepository, orderMapper, orderItemMapper);
    }

    @Test
    void getAllOrders_correctData_pageDtoReturned() {
        // GIVEN
        GetPageCommand command = new GetPageCommand(0, 10);
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
                1L,
                OrderStatus.CREATED,
                "test_payment_method",
                "test_delivery_method",
                customerDetails,
                new BigDecimal("2200.00"),
                List.of(item)
        );

        Page<Order> orderPage = new PageImpl<>(List.of(order));
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(orderPage);

        // WHEN
        PageDto<OrderDto> result = orderService.getAllOrders(command);

        // THEN
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(1, result.totalElements()),
                () -> Assertions.assertEquals(1, result.totalPages()),
                () -> Assertions.assertEquals(1, result.content().size()),
                () -> Assertions.assertEquals(1L, result.content().getFirst().id()),
                () -> Assertions.assertEquals("CREATED", result.content().getFirst().status()),
                () -> Assertions.assertEquals("test_payment_method", result.content().getFirst().paymentMethod()),
                () -> Assertions.assertEquals("test_delivery_method", result.content().getFirst().deliveryMethod()),
                () -> Assertions.assertEquals(new BigDecimal("2200.00"), result.content().getFirst().totalAmount()),
                () -> Assertions.assertEquals("test_first_name", result.content().getFirst().customerDetails().firstName()),
                () -> Assertions.assertEquals("test_last_name", result.content().getFirst().customerDetails().lastName()),
                () -> Assertions.assertEquals("test_address", result.content().getFirst().customerDetails().address()),
                () -> Assertions.assertEquals("test_email@example.com", result.content().getFirst().customerDetails().email()),
                () -> Assertions.assertEquals("123456789", result.content().getFirst().customerDetails().phoneNumber()),
                () -> Assertions.assertEquals(1, result.content().getFirst().items().size()),
                () -> Assertions.assertEquals(10L, result.content().getFirst().items().getFirst().productId()),
                () -> Assertions.assertEquals("test_product_name", result.content().getFirst().items().getFirst().productName()),
                () -> Assertions.assertEquals(new BigDecimal("1000.00"), result.content().getFirst().items().getFirst().unitPrice()),
                () -> Assertions.assertEquals(2, result.content().getFirst().items().getFirst().quantity()),
                () -> Assertions.assertEquals(1, result.content().getFirst().items().getFirst().configurations().size()),
                () -> Assertions.assertEquals("test_category", result.content().getFirst().items().getFirst().configurations().getFirst().getCategory()),
                () -> Assertions.assertEquals("test_value", result.content().getFirst().items().getFirst().configurations().getFirst().getValue()),
                () -> Assertions.assertEquals(new BigDecimal("100.00"), result.content().getFirst().items().getFirst().configurations().getFirst().getAdditionalPrice())
        );
        verify(orderRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getOrderById_orderExists_orderReturned() {
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
                OrderStatus.CREATED,
                "test_payment_method",
                "test_delivery_method",
                customerDetails,
                new BigDecimal("2200.00"),
                List.of(item)
        );

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // WHEN
        OrderDto result = orderService.getOrderById(orderId);

        // THEN
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(orderId, result.id()),
                () -> Assertions.assertEquals("CREATED", result.status()),
                () -> Assertions.assertEquals("test_payment_method", result.paymentMethod()),
                () -> Assertions.assertEquals("test_delivery_method", result.deliveryMethod()),
                () -> Assertions.assertEquals(new BigDecimal("2200.00"), result.totalAmount()),
                () -> Assertions.assertNotNull(result.customerDetails()),
                () -> Assertions.assertEquals("test_first_name", result.customerDetails().firstName()),
                () -> Assertions.assertEquals("test_last_name", result.customerDetails().lastName()),
                () -> Assertions.assertEquals("test_address", result.customerDetails().address()),
                () -> Assertions.assertEquals("test_email@example.com", result.customerDetails().email()),
                () -> Assertions.assertEquals("123456789", result.customerDetails().phoneNumber()),
                () -> Assertions.assertNotNull(result.items()),
                () -> Assertions.assertEquals(1, result.items().size()),
                () -> Assertions.assertEquals(10L, result.items().getFirst().productId()),
                () -> Assertions.assertEquals("test_product_name", result.items().getFirst().productName()),
                () -> Assertions.assertEquals(new BigDecimal("1000.00"), result.items().getFirst().unitPrice()),
                () -> Assertions.assertEquals(2, result.items().getFirst().quantity()),
                () -> Assertions.assertNotNull(result.items().getFirst().configurations()),
                () -> Assertions.assertEquals(1, result.items().getFirst().configurations().size()),
                () -> Assertions.assertEquals("test_category", result.items().getFirst().configurations().getFirst().getCategory()),
                () -> Assertions.assertEquals("test_value", result.items().getFirst().configurations().getFirst().getValue()),
                () -> Assertions.assertEquals(new BigDecimal("100.00"), result.items().getFirst().configurations().getFirst().getAdditionalPrice())
        );
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void getOrderById_orderDoesNotExist_orderNotFoundExceptionThrown() {
        // GIVEN
        Long nonExistingId = 999L;
        when(orderRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() -> orderService.getOrderById(nonExistingId))
                .extracting(OrderNotFoundException::getMessage)
                .isEqualTo("Order with ID: 999 was not found.");
        verify(orderRepository, times(1)).findById(nonExistingId);
    }

    @Test
    void createOrder_correctData_orderReturned() {
        // GIVEN
        CustomerDetails customerDetails = new CustomerDetails(
                "test_first_name",
                "test_last_name",
                "test_address",
                "test_email@example.com",
                "123456789"
        );
        SelectedConfiguration configuration = new SelectedConfiguration(
                "test_category",
                "test_value",
                new BigDecimal("100.00")
        );
        CreateOrderItemCommand itemCommand = new CreateOrderItemCommand(
                10L,
                "test_product_name",
                new BigDecimal("1000.00"),
                2,
                List.of(configuration)
        );
        CreateOrderCommand command = new CreateOrderCommand(
                "test_payment_method",
                "test_delivery_method",
                customerDetails,
                List.of(itemCommand)
        );
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L);
            return savedOrder;
        });

        // WHEN
        OrderDto result = orderService.createOrder(command);

        // THEN
        // (1000 + 100) * 2 = 2200
        BigDecimal expectedTotalAmount = new BigDecimal("2200.00");
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(1L, result.id()),
                () -> Assertions.assertEquals(OrderStatus.CREATED.name(), result.status()),
                () -> Assertions.assertEquals("test_payment_method", result.paymentMethod()),
                () -> Assertions.assertEquals("test_delivery_method", result.deliveryMethod()),
                () -> Assertions.assertEquals(expectedTotalAmount, result.totalAmount()),
                () -> Assertions.assertNotNull(result.customerDetails()),
                () -> Assertions.assertEquals("test_first_name", result.customerDetails().firstName()),
                () -> Assertions.assertEquals("test_last_name", result.customerDetails().lastName()),
                () -> Assertions.assertEquals("test_address", result.customerDetails().address()),
                () -> Assertions.assertEquals("test_email@example.com", result.customerDetails().email()),
                () -> Assertions.assertEquals("123456789", result.customerDetails().phoneNumber()),
                () -> Assertions.assertNotNull(result.items()),
                () -> Assertions.assertEquals(1, result.items().size()),
                () -> Assertions.assertEquals(10L, result.items().getFirst().productId()),
                () -> Assertions.assertEquals("test_product_name", result.items().getFirst().productName()),
                () -> Assertions.assertEquals(new BigDecimal("1000.00"), result.items().getFirst().unitPrice()),
                () -> Assertions.assertEquals(2, result.items().getFirst().quantity()),
                () -> Assertions.assertNotNull(result.items().getFirst().configurations()),
                () -> Assertions.assertEquals(1, result.items().getFirst().configurations().size()),
                () -> Assertions.assertEquals("test_category", result.items().getFirst().configurations().getFirst().getCategory()),
                () -> Assertions.assertEquals("test_value", result.items().getFirst().configurations().getFirst().getValue()),
                () -> Assertions.assertEquals(new BigDecimal("100.00"), result.items().getFirst().configurations().getFirst().getAdditionalPrice())
        );
        verify(orderRepository, times(1)).save(any(Order.class));
    }

}
