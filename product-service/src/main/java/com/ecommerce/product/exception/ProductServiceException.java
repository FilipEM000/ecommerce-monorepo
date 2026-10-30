package com.ecommerce.product.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ProductServiceException extends RuntimeException {
    private final HttpStatus status;
    public ProductServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
