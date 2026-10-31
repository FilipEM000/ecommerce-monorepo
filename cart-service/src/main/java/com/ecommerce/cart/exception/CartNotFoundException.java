package com.ecommerce.cart.exception;

import org.springframework.http.HttpStatus;

public class CartNotFoundException extends CartServiceException {
    public CartNotFoundException(Long id) {
        super("Cart with ID: " + id + " was not found", HttpStatus.NOT_FOUND);
    }
}
