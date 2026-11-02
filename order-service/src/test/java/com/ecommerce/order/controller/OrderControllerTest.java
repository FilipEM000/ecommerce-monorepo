package com.ecommerce.order.controller;

import com.ecommerce.order.command.CreateOrderCommand;
import com.ecommerce.order.command.CreateOrderItemCommand;
import com.ecommerce.order.command.GetPageCommand;
import com.ecommerce.order.dto.CustomerDetailsDto;
import com.ecommerce.order.dto.OrderDto;
import com.ecommerce.order.dto.OrderItemDto;
import com.ecommerce.order.dto.PageDto;
import com.ecommerce.order.exception.OrderNotFoundException;
import com.ecommerce.order.model.CustomerDetails;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.model.SelectedConfiguration;
import com.ecommerce.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    OrderService orderService;

    @Test
    void getAll_correctData_pageDtoReturned() throws Exception {
        // GIVEN
        CustomerDetailsDto customerDetailsDto = new CustomerDetailsDto(
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
        OrderItemDto itemDto = new OrderItemDto(
                100L,
                10L,
                "test_product_name",
                new BigDecimal("1000.00"),
                2,
                List.of(config)
        );
        OrderDto orderDto = new OrderDto(
                1L,
                OrderStatus.CREATED.name(),
                "test_payment_method",
                "test_delivery_method",
                customerDetailsDto,
                new BigDecimal("2200.00"),
                List.of(itemDto)
        );

        PageDto<OrderDto> pageDto = new PageDto<>(List.of(orderDto), 1, 1L);
        when(orderService.getAllOrders(any(GetPageCommand.class))).thenReturn(pageDto);

        // WHEN & THEN
        mockMvc.perform(get("/orders")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].status").value("CREATED"))
                .andExpect(jsonPath("$.content[0].paymentMethod").value("test_payment_method"))
                .andExpect(jsonPath("$.content[0].deliveryMethod").value("test_delivery_method"))
                .andExpect(jsonPath("$.content[0].totalAmount").value(2200.00))
                .andExpect(jsonPath("$.content[0].customerDetails.firstName").value("test_first_name"))
                .andExpect(jsonPath("$.content[0].customerDetails.lastName").value("test_last_name"))
                .andExpect(jsonPath("$.content[0].customerDetails.address").value("test_address"))
                .andExpect(jsonPath("$.content[0].customerDetails.email").value("test_email@example.com"))
                .andExpect(jsonPath("$.content[0].customerDetails.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.content[0].items[0].id").value(100))
                .andExpect(jsonPath("$.content[0].items[0].productId").value(10))
                .andExpect(jsonPath("$.content[0].items[0].productName").value("test_product_name"))
                .andExpect(jsonPath("$.content[0].items[0].unitPrice").value(1000.00))
                .andExpect(jsonPath("$.content[0].items[0].quantity").value(2))
                .andExpect(jsonPath("$.content[0].items[0].configurations[0].category").value("test_category"))
                .andExpect(jsonPath("$.content[0].items[0].configurations[0].value").value("test_value"))
                .andExpect(jsonPath("$.content[0].items[0].configurations[0].additionalPrice").value(100.00));

        verify(orderService, times(1)).getAllOrders(any(GetPageCommand.class));
    }

    @Test
    void getById_orderExists_orderReturned() throws Exception {
        // GIVEN
        Long orderId = 1L;
        CustomerDetailsDto customerDetailsDto = new CustomerDetailsDto(
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
        OrderItemDto itemDto = new OrderItemDto(
                100L,
                10L,
                "test_product_name",
                new BigDecimal("1000.00"),
                2,
                List.of(config)
        );
        OrderDto orderDto = new OrderDto(
                orderId,
                OrderStatus.CREATED.name(),
                "test_payment_method",
                "test_delivery_method",
                customerDetailsDto,
                new BigDecimal("2200.00"),
                List.of(itemDto)
        );

        when(orderService.getOrderById(orderId)).thenReturn(orderDto);

        // WHEN & THEN
        mockMvc.perform(get("/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.paymentMethod").value("test_payment_method"))
                .andExpect(jsonPath("$.deliveryMethod").value("test_delivery_method"))
                .andExpect(jsonPath("$.totalAmount").value(2200.00))
                .andExpect(jsonPath("$.customerDetails.firstName").value("test_first_name"))
                .andExpect(jsonPath("$.customerDetails.lastName").value("test_last_name"))
                .andExpect(jsonPath("$.customerDetails.address").value("test_address"))
                .andExpect(jsonPath("$.customerDetails.email").value("test_email@example.com"))
                .andExpect(jsonPath("$.customerDetails.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.items[0].id").value(100))
                .andExpect(jsonPath("$.items[0].productId").value(10))
                .andExpect(jsonPath("$.items[0].productName").value("test_product_name"))
                .andExpect(jsonPath("$.items[0].unitPrice").value(1000.00))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].configurations[0].category").value("test_category"))
                .andExpect(jsonPath("$.items[0].configurations[0].value").value("test_value"))
                .andExpect(jsonPath("$.items[0].configurations[0].additionalPrice").value(100.00));

        verify(orderService, times(1)).getOrderById(orderId);
    }

    @Test
    void getById_orderNotFound_notFoundReturned() throws Exception {
        // GIVEN
        Long nonExistingId = 999L;
        when(orderService.getOrderById(nonExistingId)).thenThrow(new OrderNotFoundException(nonExistingId));

        // WHEN & THEN
        mockMvc.perform(get("/orders/{id}", nonExistingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order with ID: 999 was not found."))
                .andExpect(jsonPath("$.status").value(404));

        verify(orderService, times(1)).getOrderById(nonExistingId);
    }

    @Test
    void create_correctData_orderReturned() throws Exception {
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
        CustomerDetailsDto customerDetailsDto = new CustomerDetailsDto(
                "test_first_name",
                "test_last_name",
                "test_address",
                "test_email@example.com",
                "123456789"
        );
        OrderItemDto itemDto = new OrderItemDto(
                100L,
                10L,
                "test_product_name",
                new BigDecimal("1000.00"),
                2,
                List.of(configuration)
        );
        OrderDto responseOrder = new OrderDto(
                1L,
                OrderStatus.CREATED.name(),
                "test_payment_method",
                "test_delivery_method",
                customerDetailsDto,
                new BigDecimal("2200.00"),
                List.of(itemDto)
        );
        when(orderService.createOrder(any(CreateOrderCommand.class))).thenReturn(responseOrder);

        // WHEN & THEN
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.paymentMethod").value("test_payment_method"))
                .andExpect(jsonPath("$.deliveryMethod").value("test_delivery_method"))
                .andExpect(jsonPath("$.totalAmount").value(2200.00))
                .andExpect(jsonPath("$.customerDetails.firstName").value("test_first_name"))
                .andExpect(jsonPath("$.customerDetails.lastName").value("test_last_name"))
                .andExpect(jsonPath("$.customerDetails.address").value("test_address"))
                .andExpect(jsonPath("$.customerDetails.email").value("test_email@example.com"))
                .andExpect(jsonPath("$.customerDetails.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.items[0].id").value(100))
                .andExpect(jsonPath("$.items[0].productId").value(10))
                .andExpect(jsonPath("$.items[0].productName").value("test_product_name"))
                .andExpect(jsonPath("$.items[0].unitPrice").value(1000.00))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].configurations[0].category").value("test_category"))
                .andExpect(jsonPath("$.items[0].configurations[0].value").value("test_value"))
                .andExpect(jsonPath("$.items[0].configurations[0].additionalPrice").value(100.00));
        verify(orderService, times(1)).createOrder(any(CreateOrderCommand.class));
    }
}
