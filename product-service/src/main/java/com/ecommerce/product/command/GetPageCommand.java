package com.ecommerce.product.command;

import com.ecommerce.product.model.ProductType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record GetPageCommand(
        Integer page,
        Integer size,
        ProductType productType
) {
    public GetPageCommand(Integer page, Integer size) {
        this(page, size, null);
    }

    public Pageable toPageable() {
        int pageNumber = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : 10;

        return PageRequest.of(pageNumber, pageSize);
    }
}
