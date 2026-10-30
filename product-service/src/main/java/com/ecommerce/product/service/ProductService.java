package com.ecommerce.product.service;

import com.ecommerce.product.command.CreateProductCommand;
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
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductConfigurationMapper productConfigurationMapper;

    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        log.info("Process of fetching product with id {} started", id);
        Product product = findProductById(id);
        log.info("Process of fetching product completed successfully");
        return productMapper.toDto(product);
    }

    @Transactional(readOnly = true)
    public PageDto<ProductDto> getAllProducts(GetPageCommand getPageCommand) {
        log.info("Process of fetching all products started");
        Page<Product> page = (getPageCommand.productType() != null)
                ? productRepository.findByProductType(getPageCommand.productType(), getPageCommand.toPageable())
                : productRepository.findAll(getPageCommand.toPageable());

        return new PageDto<>(page.map(productMapper::toDto));
    }

    @Transactional(readOnly = true)
    public List<ProductConfigurationDto> getProductConfigurations(Long id) {
        log.info("Process of fetching all possible configurations for product with ID: {}", id);
        Product product = findProductById(id);
        List<ProductConfiguration> configurations = product.getConfigurations();
        log.info("Process of fetching configurations completed successfully");

        if (configurations == null) {
            return List.of();
        }

        return configurations.stream()
                .map(productConfigurationMapper::toDto)
                .toList();
    }

    @Transactional
    public ProductDto createProduct(CreateProductCommand createProductCommand) {
        log.info("Process of creating product started");
        if (productRepository.existsByName(createProductCommand.name())) {
            throw new ProductAlreadyExistsException(createProductCommand.name());
        }
        Product product = productMapper.toEntity(createProductCommand);
        Product saved = productRepository.save(product);
        log.info("Process of creating new product completed successfully");
        return productMapper.toDto(saved);
    }

    @Transactional
    public void deleteProductById(Long id) {
        log.info("Process of deleting product started");
        Product product = findProductById(id);
        productRepository.delete(product);
        log.info("Successfully deleted product with ID: {}", id);
    }

    @Transactional
    public ProductDto updateProduct(Long id, UpdateProductCommand updateProductCommand) {
        log.info("Process of updating product with ID: {} started", id);

        Product product = findProductById(id);

        productRepository.findByName(updateProductCommand.name())
                .filter(p -> !p.getId().equals(id))
                .ifPresent(p -> {
                    throw new ProductAlreadyExistsException(updateProductCommand.name());
                });

        List<ProductConfiguration> newConfigurations = updateProductCommand.configurations() != null
                ? updateProductCommand.configurations().stream()
                .map(productConfigurationMapper::toEntity)
                .toList()
                : List.of();

        product.update(updateProductCommand, newConfigurations);

        log.info("Successfully updated product with ID: {}", id);
        return productMapper.toDto(product);
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
