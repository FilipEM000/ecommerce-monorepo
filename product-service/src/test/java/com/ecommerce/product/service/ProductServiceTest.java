package com.ecommerce.product.service;

import com.ecommerce.product.command.CreateProductCommand;
import com.ecommerce.product.command.CreateProductConfigurationCommand;
import com.ecommerce.product.command.GetPageCommand;
import com.ecommerce.product.command.UpdateProductCommand;
import com.ecommerce.product.dto.PageDto;
import com.ecommerce.product.dto.ProductConfigurationDto;
import com.ecommerce.product.dto.ProductDto;
import com.ecommerce.product.exception.ProductAlreadyExistsException;
import com.ecommerce.product.exception.ProductNotFoundException;
import com.ecommerce.product.mapper.ProductConfigurationMapper;
import com.ecommerce.product.mapper.ProductMapper;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.model.ProductConfiguration;
import com.ecommerce.product.model.ProductType;
import com.ecommerce.product.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductServiceTest {
    ProductRepository productRepository;
    ProductService productService;
    ProductMapper productMapper;
    ProductConfigurationMapper productConfigurationMapper;

    @BeforeEach
    void setup() {
        this.productRepository = Mockito.mock(ProductRepository.class);
        this.productConfigurationMapper = Mappers.getMapper(ProductConfigurationMapper.class);
        this.productMapper = Mappers.getMapper(ProductMapper.class);
        ReflectionTestUtils.setField(productMapper, "productConfigurationMapper", productConfigurationMapper);
        this.productService = new ProductService(productRepository, productMapper, productConfigurationMapper);
    }

    @Test
    void getProductById_productExists_ProductReturned() {
        // GIVEN
        Long productId = 1L;
        ProductConfiguration config = new ProductConfiguration(
                10L,
                "test_category",
                "test_value",
                true,
                new BigDecimal("100.00")
        );
        Product product = new Product(
                productId,
                "test_name",
                "test_description",
                ProductType.COMPUTER,
                50,
                new BigDecimal("1000.00"),
                List.of(config)
        );
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // WHEN
        ProductDto result = productService.getProductById(productId);

        // THEN
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(productId, result.id()),
                () -> Assertions.assertEquals("test_name", result.name()),
                () -> Assertions.assertEquals("test_description", result.description()),
                () -> Assertions.assertEquals(ProductType.COMPUTER, result.productType()),
                () -> Assertions.assertEquals(new BigDecimal("1000.00"), result.basePrice()),
                () -> Assertions.assertEquals(50, result.totalQuantity()),
                () -> Assertions.assertNotNull(result.configurations()),
                () -> Assertions.assertEquals(1, result.configurations().size()),
                () -> Assertions.assertEquals(10L, result.configurations().getFirst().id()),
                () -> Assertions.assertEquals("test_category", result.configurations().getFirst().category()),
                () -> Assertions.assertEquals("test_value", result.configurations().getFirst().value()),
                () -> Assertions.assertEquals(new BigDecimal("100.00"), result.configurations().getFirst().additionalPrice()),
                () -> Assertions.assertEquals(true, result.configurations().getFirst().isDefault())
        );
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void getProductById_productDoesNotExist_productNotFoundExceptionThrown() {
        // GIVEN
        Long nonExistingId = 1L;
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> productService.getProductById(nonExistingId))
                .extracting(ProductNotFoundException::getMessage)
                .isEqualTo("Product with ID: 1 was not found");
        verify(productRepository).findById(nonExistingId);
    }

    @Test
    void getAllProducts_dataCorrect_pageDtoReturned() {
        // GIVEN
        GetPageCommand command = new GetPageCommand(0, 10);
        ProductConfiguration config = new ProductConfiguration(
                10L,
                "test_category",
                "test_value",
                true,
                new BigDecimal("100.00")
        );
        Product product = new Product(
                1L,
                "test_name",
                "test_description",
                ProductType.COMPUTER,
                50,
                new BigDecimal("1000.00"),
                List.of(config)
        );
        Page<Product> productPage = new PageImpl<>(List.of(product), command.toPageable(), 1);
        when(productRepository.findAll(command.toPageable())).thenReturn(productPage);

        // WHEN
        PageDto<ProductDto> result = productService.getAllProducts(command);

        // THEN
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(1, result.totalPages()),
                () -> Assertions.assertEquals(1L, result.totalElements()),
                () -> Assertions.assertNotNull(result.content()),
                () -> Assertions.assertEquals(1, result.content().size()),
                () -> Assertions.assertEquals(1L, result.content().getFirst().id()),
                () -> Assertions.assertEquals("test_name", result.content().getFirst().name()),
                () -> Assertions.assertEquals("test_description", result.content().getFirst().description()),
                () -> Assertions.assertEquals(ProductType.COMPUTER, result.content().getFirst().productType()),
                () -> Assertions.assertEquals(new BigDecimal("1000.00"), result.content().getFirst().basePrice()),
                () -> Assertions.assertEquals(50, result.content().getFirst().totalQuantity()),
                () -> Assertions.assertNotNull(result.content().getFirst().configurations()),
                () -> Assertions.assertEquals(1, result.content().getFirst().configurations().size()),
                () -> Assertions.assertEquals(10L, result.content().getFirst().configurations().getFirst().id()),
                () -> Assertions.assertEquals("test_category", result.content().getFirst().configurations().getFirst().category()),
                () -> Assertions.assertEquals("test_value", result.content().getFirst().configurations().getFirst().value()),
                () -> Assertions.assertEquals(new BigDecimal("100.00"), result.content().getFirst().configurations().getFirst().additionalPrice()),
                () -> Assertions.assertEquals(true, result.content().getFirst().configurations().getFirst().isDefault())
        );
        verify(productRepository).findAll(command.toPageable());
    }

    @Test
    void getAllProducts_withProductTypeFilter_filteredPageDtoReturned() {
        // GIVEN
        GetPageCommand command = new GetPageCommand(0, 10, ProductType.COMPUTER);
        Product product = new Product(
                1L,
                "test_name",
                "test_description",
                ProductType.COMPUTER,
                50,
                new BigDecimal("1000.00"),
                List.of()
        );
        Page<Product> productPage = new PageImpl<>(List.of(product), command.toPageable(), 1);
        when(productRepository.findByProductType(ProductType.COMPUTER, command.toPageable())).thenReturn(productPage);

        // WHEN
        PageDto<ProductDto> result = productService.getAllProducts(command);

        // THEN
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(1, result.totalPages()),
                () -> Assertions.assertEquals(1L, result.totalElements()),
                () -> Assertions.assertEquals(1, result.content().size()),
                () -> Assertions.assertEquals(ProductType.COMPUTER, result.content().getFirst().productType())
        );
        verify(productRepository).findByProductType(ProductType.COMPUTER, command.toPageable());
        verify(productRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getProductConfigurations_productExists_configurationsReturned() {
        // GIVEN
        Long productId = 1L;
        ProductConfiguration config = new ProductConfiguration(
                10L,
                "test_category",
                "test_value",
                true,
                new BigDecimal("100.00")
        );
        Product product = new Product(
                productId,
                "test_name",
                "test_description",
                ProductType.COMPUTER,
                50,
                new BigDecimal("1000.00"),
                List.of(config)
        );
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // WHEN
        List<ProductConfigurationDto> result = productService.getProductConfigurations(productId);

        // THEN
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(1, result.size()),
                () -> Assertions.assertEquals(10L, result.getFirst().id()),
                () -> Assertions.assertEquals("test_category", result.getFirst().category()),
                () -> Assertions.assertEquals("test_value", result.getFirst().value()),
                () -> Assertions.assertEquals(new BigDecimal("100.00"), result.getFirst().additionalPrice()),
                () -> Assertions.assertEquals(true, result.getFirst().isDefault())
        );
        verify(productRepository).findById(productId);
    }

    @Test
    void getProductConfigurations_productDoesNotExist_productNotFoundExceptionThrown() {
        // GIVEN
        Long nonExistingId = 1L;
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> productService.getProductConfigurations(nonExistingId))
                .extracting(ProductNotFoundException::getMessage)
                .isEqualTo("Product with ID: 1 was not found");
        verify(productRepository).findById(nonExistingId);
    }

    @Test
    void createProduct_dataCorrect_ProductCreated() {
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
        ProductConfiguration savedConfig = new ProductConfiguration(
                10L,
                "test_category",
                "test_value",
                true,
                new BigDecimal("100.00")
        );
        Product savedProduct = new Product(
                1L,
                "test_name",
                "test_description",
                ProductType.COMPUTER,
                50,
                new BigDecimal("1000.00"),
                List.of(savedConfig)
        );
        when(productRepository.existsByName(command.name())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // WHEN
        ProductDto result = productService.createProduct(command);

        // THEN
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(1L, result.id()),
                () -> Assertions.assertEquals("test_name", result.name()),
                () -> Assertions.assertEquals("test_description", result.description()),
                () -> Assertions.assertEquals(ProductType.COMPUTER, result.productType()),
                () -> Assertions.assertEquals(new BigDecimal("1000.00"), result.basePrice()),
                () -> Assertions.assertEquals(50, result.totalQuantity()),
                () -> Assertions.assertNotNull(result.configurations()),
                () -> Assertions.assertEquals(1, result.configurations().size()),
                () -> Assertions.assertEquals(10L, result.configurations().getFirst().id()),
                () -> Assertions.assertEquals("test_category", result.configurations().getFirst().category()),
                () -> Assertions.assertEquals("test_value", result.configurations().getFirst().value()),
                () -> Assertions.assertEquals(new BigDecimal("100.00"), result.configurations().getFirst().additionalPrice()),
                () -> Assertions.assertEquals(true, result.configurations().getFirst().isDefault())
        );
        verify(productRepository, times(1)).existsByName(command.name());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_productAlreadyExists_ProductAlreadyExistsExceptionThrown() {
        //GIVEN
        CreateProductCommand createProductCommand = new CreateProductCommand(
                "test_name",
                "test_description",
                ProductType.COMPUTER,
                new BigDecimal("1000.00"),
                50,
                List.of()
        );
        when(productRepository.existsByName(any())).thenReturn(true);

        // WHEN & THEN
        assertThatExceptionOfType(ProductAlreadyExistsException.class)
                .isThrownBy(() -> productService.createProduct(createProductCommand))
                .extracting(ProductAlreadyExistsException::getMessage)
                .isEqualTo("Product with name 'test_name' already exists");

        verify(productRepository).existsByName(createProductCommand.name());
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProductById_dataCorrect_productDeleted() {
        // GIVEN
        Long productId = 1L;
        Product product = new Product(
                productId,
                "test_name",
                "test_description",
                ProductType.COMPUTER,
                50,
                new BigDecimal("1000.00"),
                List.of()
        );
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // WHEN
        productService.deleteProductById(productId);

        // THEN
        verify(productRepository).findById(productId);
        verify(productRepository).delete(product);
    }

    @Test
    void deleteProductById_productDoesNotExist_productNotFoundExceptionThrown() {
        // GIVEN
        Long nonExistingId = 1L;
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> productService.deleteProductById(nonExistingId))
                .extracting(ProductNotFoundException::getMessage)
                .isEqualTo("Product with ID: 1 was not found");
        verify(productRepository).findById(nonExistingId);
        verify(productRepository, never()).delete(any());
    }

    @Test
    void updateProduct_dataCorrect_productUpdated() {
        // GIVEN
        Long productId = 1L;
        CreateProductConfigurationCommand newConfigCommand = new CreateProductConfigurationCommand(
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
                List.of(newConfigCommand)
        );
        ProductConfiguration existingConfig = new ProductConfiguration(
                10L,
                "test_category",
                "test_value",
                true,
                new BigDecimal("100.00")
        );
        Product existingProduct = new Product(
                productId,
                "test_name",
                "test_description",
                ProductType.COMPUTER,
                50,
                new BigDecimal("1000.00"),
                new ArrayList<>(List.of(existingConfig))
        );
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.findByName(command.name())).thenReturn(Optional.empty());

        // WHEN
        ProductDto result = productService.updateProduct(productId, command);

        // THEN
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(productId, result.id()),
                () -> Assertions.assertEquals("test_updated_name", result.name()),
                () -> Assertions.assertEquals("test_updated_description", result.description()),
                () -> Assertions.assertEquals(ProductType.SMARTPHONE, result.productType()),
                () -> Assertions.assertEquals(new BigDecimal("1500.00"), result.basePrice()),
                () -> Assertions.assertEquals(30, result.totalQuantity()),
                () -> Assertions.assertNotNull(result.configurations()),
                () -> Assertions.assertEquals(1, result.configurations().size()),
                () -> Assertions.assertEquals("test_updated_category", result.configurations().getFirst().category()),
                () -> Assertions.assertEquals("test_updated_value", result.configurations().getFirst().value()),
                () -> Assertions.assertEquals(new BigDecimal("150.00"), result.configurations().getFirst().additionalPrice()),
                () -> Assertions.assertEquals(true, result.configurations().getFirst().isDefault())
        );
        verify(productRepository).findById(productId);
        verify(productRepository).findByName(command.name());
    }

    @Test
    void updateProduct_productDoesNotExist_productNotFoundExceptionThrown() {
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
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> productService.updateProduct(nonExistingId, command))
                .extracting(ProductNotFoundException::getMessage)
                .isEqualTo("Product with ID: 1 was not found");
        verify(productRepository).findById(nonExistingId);
        verify(productRepository, never()).findByName(any());
    }

    @Test
    void updateProduct_productAlreadyExists_productAlreadyExistsExceptionThrown() {
        // GIVEN
        Long productId = 1L;
        Long otherProductId = 2L;
        UpdateProductCommand command = new UpdateProductCommand(
                "test_name",
                "test_description",
                ProductType.COMPUTER,
                new BigDecimal("1000.00"),
                50,
                List.of()
        );
        Product existingProduct = new Product(
                productId,
                "test_old_name",
                "test_description",
                ProductType.COMPUTER,
                50,
                new BigDecimal("1000.00"),
                new ArrayList<>()
        );
        Product otherProductWithSameName = new Product(
                otherProductId,
                "test_name",
                "test_description",
                ProductType.COMPUTER,
                50,
                new BigDecimal("1000.00"),
                new ArrayList<>()
        );
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.findByName(command.name())).thenReturn(Optional.of(otherProductWithSameName));

        // WHEN & THEN
        assertThatExceptionOfType(ProductAlreadyExistsException.class)
                .isThrownBy(() -> productService.updateProduct(productId, command))
                .extracting(ProductAlreadyExistsException::getMessage)
                .isEqualTo("Product with name 'test_name' already exists");
        verify(productRepository).findById(productId);
        verify(productRepository).findByName(command.name());
    }
}