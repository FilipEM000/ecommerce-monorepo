package com.ecommerce.order.exception.handler;

import com.ecommerce.order.dto.ErrorResponse;
import com.ecommerce.order.exception.OrderServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleOrderServiceException(OrderServiceException exception) {
        log.warn("Exception occured: {}", exception.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                exception.getMessage(),
                exception.getStatus().value(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(exception.getStatus()).body(errorResponse);
    }
}
