package com.ecommerce.product.exception;

import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends ProductServiceException {
    public ProductNotFoundException(Long id) {
        super("Product with ID: " + id + " was not found", HttpStatus.NOT_FOUND);
    }
}
