package com.ecommerce.order.exception;

import org.springframework.http.HttpStatus;

public class InvalidOrderStatusTransitionException extends OrderServiceException {
    public InvalidOrderStatusTransitionException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
