package com.ecommerce.product.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageDto<T>(
        List<T> content,
        Integer totalPages,
        Long totalElements
) {
    public PageDto(Page<T> page) {
        this(page.getContent(), page.getTotalPages(), page.getTotalElements());
    }
}
