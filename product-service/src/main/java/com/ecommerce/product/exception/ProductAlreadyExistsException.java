package com.ecommerce.product.exception;

import org.springframework.http.HttpStatus;

public class ProductAlreadyExistsException extends ProductServiceException {
    public ProductAlreadyExistsException(String message) {
        super("Product with name '" + message + "' already exists", HttpStatus.CONFLICT);
    }
}
