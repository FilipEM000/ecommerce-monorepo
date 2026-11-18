package com.ecommerce.order.exception;

import org.springframework.http.HttpStatus;

public class CannotGenerateInvoiceException extends OrderServiceException {
    public CannotGenerateInvoiceException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
