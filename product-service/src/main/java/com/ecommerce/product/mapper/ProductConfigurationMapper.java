package com.ecommerce.product.mapper;

import com.ecommerce.product.command.CreateProductConfigurationCommand;
import com.ecommerce.product.dto.ProductConfigurationDto;
import com.ecommerce.product.model.ProductConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductConfigurationMapper {

    ProductConfigurationDto toDto(ProductConfiguration productConfiguration);

    ProductConfiguration toEntity(ProductConfigurationDto productConfigurationDto);

    @Mapping(target = "id", ignore = true)
    ProductConfiguration toEntity(CreateProductConfigurationCommand createProductConfigurationCommand);
}
