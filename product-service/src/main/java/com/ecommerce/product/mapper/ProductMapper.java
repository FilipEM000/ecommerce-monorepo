package com.ecommerce.product.mapper;

import com.ecommerce.product.command.CreateProductCommand;
import com.ecommerce.product.dto.ProductDto;
import com.ecommerce.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductConfigurationMapper.class})
public interface ProductMapper {

    ProductDto toDto(Product product);

    Product toEntity(ProductDto productDto);

    @Mapping(target = "id", ignore = true)
    Product toEntity(CreateProductCommand createProductCommand);
}
