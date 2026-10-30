package com.ecommerce.product.controller;

import com.ecommerce.product.command.CreateProductCommand;
import com.ecommerce.product.command.GetPageCommand;
import com.ecommerce.product.command.UpdateProductCommand;
import com.ecommerce.product.dto.ErrorResponse;
import com.ecommerce.product.dto.PageDto;
import com.ecommerce.product.dto.ProductConfigurationDto;
import com.ecommerce.product.dto.ProductDto;
import com.ecommerce.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "Get product by ID", description = "Fetches detailed product data along with its configurations.")
    @ApiResponse(responseCode = "200", description = "Product found")
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable Long id) {
        log.info("Received request to fetch product with ID: {}", id);
        return productService.getProductById(id);
    }

    @Operation(summary = "Get all products", description = "Fetches a paginated list of products, optionally filtered by product type.")
    @ApiResponse(responseCode = "200", description = "Products page retrieved successfully")
    @GetMapping
    public PageDto<ProductDto> getAll(GetPageCommand command) {
        log.info("Received request to fetch products with pagination: {}", command);
        return productService.getAllProducts(command);
    }

    @Operation(summary = "Get product configurations", description = "Fetches all available configuration options for a specific product.")
    @ApiResponse(responseCode = "200", description = "Configurations retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}/configurations")
    public List<ProductConfigurationDto> getProductConfigurations(@PathVariable Long id) {
        log.info("Received request to fetch all configuration options for product with ID: {}", id);
        return productService.getProductConfigurations(id);
    }

    @Operation(summary = "Create product", description = "Creates a new product with optional configurations.")
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Product already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto create(@Valid @RequestBody CreateProductCommand createProductCommand) {
        log.info("Received request to create new product");
        return productService.createProduct(createProductCommand);
    }

    @Operation(summary = "Delete product", description = "Deletes a product by ID.")
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Received request to delete product with ID: {}", id);
        productService.deleteProductById(id);
    }

    @Operation(summary = "Update product", description = "Updates an existing product and replaces its configuration list.")
    @ApiResponse(responseCode = "200", description = "Product updated successfully")
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    public ProductDto update(@PathVariable Long id, @Valid @RequestBody UpdateProductCommand updateProductCommand) {
        log.info("Received request to update product with ID: {}", id);
        return productService.updateProduct(id, updateProductCommand);
    }
}
