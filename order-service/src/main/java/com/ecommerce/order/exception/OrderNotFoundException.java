package com.ecommerce.order.exception;

import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends OrderServiceException {
    public OrderNotFoundException(Long id) {
        super("Order with ID: " + id + " was not found.", HttpStatus.NOT_FOUND);
    }
}
