package com.ecommerce.cart.exception.handler;

import com.ecommerce.cart.dto.ErrorResponse;
import com.ecommerce.cart.exception.CartServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CartServiceException.class)
    public ResponseEntity<ErrorResponse> handleCartServiceException(CartServiceException exception) {
        log.error("CartServiceException occurred: {}", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                exception.getMessage(),
                exception.getHttpStatus().value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(exception.getHttpStatus()).body(errorResponse);
    }
}