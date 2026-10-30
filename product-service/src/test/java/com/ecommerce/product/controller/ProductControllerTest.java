package com.ecommerce.product.controller;

import com.ecommerce.product.command.CreateProductCommand;
import com.ecommerce.product.command.CreateProductConfigurationCommand;
import com.ecommerce.product.command.GetPageCommand;
import com.ecommerce.product.command.UpdateProductCommand;
import com.ecommerce.product.dto.PageDto;
import com.ecommerce.product.dto.ProductConfigurationDto;
import com.ecommerce.product.dto.ProductDto;
import com.ecommerce.product.exception.ProductAlreadyExistsException;
import com.ecommerce.product.exception.ProductNotFoundException;
import com.ecommerce.product.model.ProductType;
import com.ecommerce.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ProductService productService;

    @Test
    void getById_dataCorrect_productReturned() throws Exception {
        // GIVEN
        Long productId = 1L;
        ProductConfigurationDto configDto = new ProductConfigurationDto(
                10L,
                "test_category",
                "test_value",
                new BigDecimal("100.00"),
                true
        );
        ProductDto product = new ProductDto(
                productId,
                "test_name",
                "test_description",
                ProductType.COMPUTER,
                new BigDecimal("1000.00"),
                50,
                List.of(configDto)
        );
        when(productService.getProductById(productId)).thenReturn(product);

        // WHEN & THEN
        mockMvc.perform(get("/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test_name"))
                .andExpect(jsonPath("$.description").value("test_description"))
                .andExpect(jsonPath("$.productType").value("COMPUTER"))
                .andExpect(jsonPath("$.basePrice").value(1000.00))
                .andExpect(jsonPath("$.totalQuantity").value(50))
                .andExpect(jsonPath("$.configurations[0].id").value(10))
                .andExpect(jsonPath("$.configurations[0].category").value("test_category"))
                .andExpect(jsonPath("$.configurations[0].value").value("test_value"))
                .andExpect(jsonPath("$.configurations[0].additionalPrice").value(100.00))
                .andExpect(jsonPath("$.configurations[0].isDefault").value(true));
        verify(productService).getProductById(productId);
    }

    @Test
    void getById_productDoesNotExist_NotFoundReturned() throws Exception {
        // GIVEN
        Long nonExistingId = 1L;
        when(productService.getProductById(nonExistingId))
                .thenThrow(new ProductNotFoundException(nonExistingId));

        // WHEN & THEN
        mockMvc.perform(get("/products/{id}", nonExistingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product with ID: 1 was not found"))
                .andExpect(jsonPath("$.timestamp").exists());
        verify(productService).getProductById(nonExistingId);
    }

    @Test
    void getAll_dataCorrect_pageDtoReturned() throws Exception {
        // GIVEN
        ProductConfigurationDto configDto = new ProductConfigurationDto(
                10L,
                "test_category",
                "test_value",
                new BigDecimal("100.00"),
                true
        );
        ProductDto productDto = new ProductDto(
                1L,
                "test_name",
                "test_description",
                ProductType.COMPUTER,
                new BigDecimal("1000.00"),
                50,
                List.of(configDto)
        );
        PageDto<ProductDto> expectedPageDto = new PageDto<>(List.of(productDto), 1, 1L);
        when(productService.getAllProducts(any(GetPageCommand.class))).thenReturn(expectedPageDto);

        // WHEN & THEN
        mockMvc.perform(get("/products")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("test_name"))
                .andExpect(jsonPath("$.content[0].description").value("test_description"))
                .andExpect(jsonPath("$.content[0].productType").value("COMPUTER"))
                .andExpect(jsonPath("$.content[0].basePrice").value(1000.00))
                .andExpect(jsonPath("$.content[0].totalQuantity").value(50))
                .andExpect(jsonPath("$.content[0].configurations[0].id").value(10))
                .andExpect(jsonPath("$.content[0].configurations[0].category").value("test_category"))
                .andExpect(jsonPath("$.content[0].configurations[0].value").value("test_value"))
                .andExpect(jsonPath("$.content[0].configurations[0].additionalPrice").value(100.00))
                .andExpect(jsonPath("$.content[0].configurations[0].isDefault").value(true));
        verify(productService).getAllProducts(any(GetPageCommand.class));
    }

    @Test
    void getAll_withProductTypeFilter_filteredPageDtoReturned() throws Exception {
        // GIVEN
        ProductDto productDto = new ProductDto(
                1L,
                "test_name",
                "test_description",
                ProductType.COMPUTER,
                new BigDecimal("1000.00"),
                50,
                List.of()
        );
        PageDto<ProductDto> expectedPageDto = new PageDto<>(List.of(productDto), 1, 1L);
        when(productService.getAllProducts(any(GetPageCommand.class))).thenReturn(expectedPageDto);

        // WHEN & THEN
        mockMvc.perform(get("/products")
                        .param("page", "0")
                        .param("size", "10")
                        .param("productType", "COMPUTER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].productType").value("COMPUTER"));
        verify(productService).getAllProducts(any(GetPageCommand.class));
    }

    @Test
    void getProductConfigurations_dataCorrect_configurationsReturned() throws Exception {
        // GIVEN
        Long productId = 1L;
        ProductConfigurationDto configDto = new ProductConfigurationDto(
                10L,
                "test_category",
                "test_value",
                new BigDecimal("100.00"),
                true
        );
        when(productService.getProductConfigurations(productId)).thenReturn(List.of(configDto));

        // WHEN & THEN
        mockMvc.perform(get("/products/{id}/configurations", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].category").value("test_category"))
                .andExpect(jsonPath("$[0].value").value("test_value"))
                .andExpect(jsonPath("$[0].additionalPrice").value(100.00))
                .andExpect(jsonPath("$[0].isDefault").value(true));
        verify(productService).getProductConfigurations(productId);
    }

    @Test
    void getProductConfigurations_productDoesNotExist_notFoundReturned() throws Exception {
        // GIVEN
        Long nonExistingId = 1L;
        when(productService.getProductConfigurations(nonExistingId))
                .thenThrow(new ProductNotFoundException(nonExistingId));

        // WHEN & THEN
        mockMvc.perform(get("/products/{id}/configurations", nonExistingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product with ID: 1 was not found"))
                .andExpect(jsonPath("$.timestamp").exists());
        verify(productService).getProductConfigurations(nonExistingId);
    }

    @Test
    void create_dataCorrect_productCreated() throws Exception {
        // GIVEN
        CreateProductConfigurationCommand configCommand = new CreateProductConfigurationCommand(
                "test_category",
                "test_value",
                new BigDecimal("100.00"),
                true
        );
        CreateProductCommand command = new CreateProductCommand(
                "test_name",
                "test_description",
                ProductType.COMPUTER,
                new BigDecimal("1000.00"),
                50,
                List.of(configCommand)
        );
        ProductConfigurationDto expectedConfigDto = new ProductConfigurationDto(
                10L,
                "test_category",
                "test_value",
                new BigDecimal("100.00"),
                true
        );
        ProductDto expectedProductDto = new ProductDto(
                1L,
                "test_name",
                "test_description",
                ProductType.COMPUTER,
                new BigDecimal("1000.00"),
                50,
                List.of(expectedConfigDto)
        );
        when(productService.createProduct(command)).thenReturn(expectedProductDto);

        // WHEN & THEN
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test_name"))
                .andExpect(jsonPath("$.description").value("test_description"))
                .andExpect(jsonPath("$.productType").value("COMPUTER"))
                .andExpect(jsonPath("$.basePrice").value(1000.00))
                .andExpect(jsonPath("$.totalQuantity").value(50))
                .andExpect(jsonPath("$.configurations[0].id").value(10))
                .andExpect(jsonPath("$.configurations[0].category").value("test_category"))
                .andExpect(jsonPath("$.configurations[0].value").value("test_value"))
                .andExpect(jsonPath("$.configurations[0].additionalPrice").value(100.00))
                .andExpect(jsonPath("$.configurations[0].isDefault").value(true));
        verify(productService).createProduct(command);
    }

    @Test
    void create_productAlreadyExists_conflictReturned() throws Exception {
        // GIVEN
        CreateProductCommand command = new CreateProductCommand(
                "test_name",
                "test_description",
                ProductType.COMPUTER,
                new BigDecimal("1000.00"),
                50,
                List.of()
        );
        when(productService.createProduct(command))
                .thenThrow(new ProductAlreadyExistsException("test_name"));

        // WHEN & THEN
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Product with name 'test_name' already exists"))
                .andExpect(jsonPath("$.timestamp").exists());
        verify(productService).createProduct(command);
    }

    @Test
    void create_nameBlank_BadRequestReturned() throws Exception {
        // GIVEN
        CreateProductCommand command = new CreateProductCommand(
                "",
                "test_description",
                ProductType.COMPUTER,
                new BigDecimal("1000.00"),
                50,
                List.of()
        );

        // WHEN & THEN
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
        verify(productService, never()).createProduct(any());
    }

    @Test
    void delete_dataCorrect_noContentReturned() throws Exception {
        // GIVEN
        Long productId = 1L;
        doNothing().when(productService).deleteProductById(productId);

        // WHEN & THEN
        mockMvc.perform(delete("/products/{id}", productId))
                .andExpect(status().isNoContent());
        verify(productService).deleteProductById(productId);
    }

    @Test
    void delete_productDoesNotExist_notFoundReturned() throws Exception {
        // GIVEN
        Long nonExistingId = 1L;
        doThrow(new ProductNotFoundException(nonExistingId))
                .when(productService).deleteProductById(nonExistingId);

        // WHEN & THEN
        mockMvc.perform(delete("/products/{id}", nonExistingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product with ID: 1 was not found"))
                .andExpect(jsonPath("$.timestamp").exists());
        verify(productService).deleteProductById(nonExistingId);
    }

    @Test
    void update_dataCorrect_productUpdated() throws Exception {
        // GIVEN
        Long productId = 1L;
        CreateProductConfigurationCommand configCommand = new CreateProductConfigurationCommand(
                "test_updated_category",
                "test_updated_value",
                new BigDecimal("150.00"),
                true
        );
        UpdateProductCommand command = new UpdateProductCommand(
                "test_updated_name",
                "test_updated_description",
                ProductType.SMARTPHONE,
                new BigDecimal("1500.00"),
                30,
                List.of(configCommand)
        );
        ProductConfigurationDto expectedConfigDto = new ProductConfigurationDto(
                10L,
                "test_updated_category",
                "test_updated_value",
                new BigDecimal("150.00"),
                true
        );
        ProductDto expectedProductDto = new ProductDto(
                productId,
                "test_updated_name",
                "test_updated_description",
                ProductType.SMARTPHONE,
                new BigDecimal("1500.00"),
                30,
                List.of(expectedConfigDto)
        );
        when(productService.updateProduct(productId, command)).thenReturn(expectedProductDto);

        // WHEN & THEN
        mockMvc.perform(put("/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test_updated_name"))
                .andExpect(jsonPath("$.description").value("test_updated_description"))
                .andExpect(jsonPath("$.productType").value("SMARTPHONE"))
                .andExpect(jsonPath("$.basePrice").value(1500.00))
                .andExpect(jsonPath("$.totalQuantity").value(30))
                .andExpect(jsonPath("$.configurations[0].id").value(10))
                .andExpect(jsonPath("$.configurations[0].category").value("test_updated_category"))
                .andExpect(jsonPath("$.configurations[0].value").value("test_updated_value"))
                .andExpect(jsonPath("$.configurations[0].additionalPrice").value(150.00))
                .andExpect(jsonPath("$.configurations[0].isDefault").value(true));
        verify(productService).updateProduct(productId, command);
    }

    @Test
    void update_productDoesNotExist_notFoundReturned() throws Exception {
        // GIVEN
        Long nonExistingId = 1L;
        UpdateProductCommand command = new UpdateProductCommand(
                "test_name",
                "test_description",
                ProductType.COMPUTER,
                new BigDecimal("1000.00"),
                50,
                List.of()
        );
        when(productService.updateProduct(nonExistingId, command))
                .thenThrow(new ProductNotFoundException(nonExistingId));

        // WHEN & THEN
        mockMvc.perform(put("/products/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product with ID: 1 was not found"))
                .andExpect(jsonPath("$.timestamp").exists());
        verify(productService).updateProduct(nonExistingId, command);
    }

    @Test
    void update_productAlreadyExists_conflictReturned() throws Exception {
        // GIVEN
        Long productId = 1L;
        UpdateProductCommand command = new UpdateProductCommand(
                "test_name",
                "test_description",
                ProductType.COMPUTER,
                new BigDecimal("1000.00"),
                50,
                List.of()
        );
        when(productService.updateProduct(productId, command))
                .thenThrow(new ProductAlreadyExistsException("test_name"));

        // WHEN & THEN
        mockMvc.perform(put("/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Product with name 'test_name' already exists"))
                .andExpect(jsonPath("$.timestamp").exists());
        verify(productService).updateProduct(productId, command);
    }
}