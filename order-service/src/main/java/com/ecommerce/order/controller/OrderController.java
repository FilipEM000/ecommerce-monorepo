package com.ecommerce.order.controller;

import com.ecommerce.order.command.CreateOrderCommand;
import com.ecommerce.order.command.GetPageCommand;
import com.ecommerce.order.dto.ErrorResponse;
import com.ecommerce.order.dto.OrderDto;
import com.ecommerce.order.dto.PageDto;
import com.ecommerce.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@Slf4j
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Create order", description = "Places a new order with purchased items, configurations, and customer delivery details.")
    @ApiResponse(responseCode = "201", description = "Order created successfully")
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto create(@Valid @RequestBody CreateOrderCommand createOrderCommand) {
        log.info("Received request to create new order");
        return orderService.createOrder(createOrderCommand);
    }

    @Operation(summary = "Get all orders", description = "Retrieves a paginated list of order history.")
    @ApiResponse(responseCode = "200", description = "Orders page retrieved successfully")
    @GetMapping
    public PageDto<OrderDto> getAll(GetPageCommand getPageCommand) {
        log.info("Received request to get all orders with pagination: {}", getPageCommand);
        return orderService.getAllOrders(getPageCommand);
    }

    @Operation(summary = "Get order by ID", description = "Retrieves detailed order information including status, payment/delivery method, customer details, and items.")
    @ApiResponse(responseCode = "200", description = "Order retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public OrderDto getById(@PathVariable Long id) {
        log.info("Received request to get order with ID: {}", id);
        return orderService.getOrderById(id);
    }
}
