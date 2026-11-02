package com.ecommerce.order.service;

import com.ecommerce.order.command.CreateOrderCommand;
import com.ecommerce.order.command.GetPageCommand;
import com.ecommerce.order.dto.OrderDto;
import com.ecommerce.order.dto.PageDto;
import com.ecommerce.order.exception.OrderNotFoundException;
import com.ecommerce.order.mapper.OrderItemMapper;
import com.ecommerce.order.mapper.OrderMapper;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Transactional
    public OrderDto createOrder(CreateOrderCommand createOrderCommand) {
        log.info("Process of creating order started");
        Order order = orderMapper.toEntity(createOrderCommand);
        BigDecimal totalAmount = order.getItems().stream()
                .map(OrderItem::getTotalItemPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.CREATED);
        Order saved = orderRepository.save(order);
        log.info("Process of creating order completed successfully");
        return orderMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public PageDto<OrderDto> getAllOrders(GetPageCommand getPageCommand) {
        log.info("Process of fetching all orders started");
        Page<OrderDto> page = orderRepository.findAll(getPageCommand.toPageable())
                .map(orderMapper::toDto);
        log.info("Process of fetching orders completed successfully");
        return new PageDto<>(page);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long id) {
        log.info("Process of fetching order with ID: {} started", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        log.info("Process of fetching order completed successfully");
        return orderMapper.toDto(order);
    }

}
