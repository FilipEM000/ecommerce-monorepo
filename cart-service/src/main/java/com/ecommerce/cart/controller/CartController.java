package com.ecommerce.cart.controller;

import com.ecommerce.cart.command.AddCartItemCommand;
import com.ecommerce.cart.dto.CartDto;
import com.ecommerce.cart.dto.ErrorResponse;
import com.ecommerce.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {
    private final CartService cartService;

    @Operation(summary = "Get cart by ID", description = "Retrieves cart details including items, their configurations, and the total price.")
    @ApiResponse(responseCode = "200", description = "Cart found successfully")
    @ApiResponse(responseCode = "404", description = "Cart not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public CartDto getCartById(@PathVariable Long id) {
        log.info("Received request to get cart with ID: {}", id);
        return cartService.getCartById(id);
    }

    @Operation(summary = "Create empty cart", description = "Creates a new empty shopping cart and returns its identifier.")
    @ApiResponse(responseCode = "201", description = "Cart created successfully")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartDto create() {
        log.info("Received request to create a new cart");
        return cartService.createCart();
    }

    @Operation(summary = "Add item to cart", description = "Adds a product variant with selected configurations to the cart, or increases the quantity if the exact variant already exists.")
    @ApiResponse(responseCode = "201", description = "Item added to cart successfully")
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Cart not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("{id}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public CartDto addItem(@PathVariable Long id, @Valid @RequestBody AddCartItemCommand addCartItemCommand) {
        log.info("Received request to add product to cart");
        return cartService.addProductToCart(id, addCartItemCommand);
    }
}
