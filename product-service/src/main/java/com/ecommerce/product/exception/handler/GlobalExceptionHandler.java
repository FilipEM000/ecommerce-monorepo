package com.ecommerce.product.exception.handler;

import com.ecommerce.product.dto.ErrorResponse;
import com.ecommerce.product.exception.ProductServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductServiceException.class)
    public ResponseEntity<ErrorResponse> handleProductServiceException(ProductServiceException exception) {
        log.warn("Exception occured: {}", exception.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                exception.getMessage(),
                exception.getStatus().value(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(exception.getStatus()).body(errorResponse);
    }
}
