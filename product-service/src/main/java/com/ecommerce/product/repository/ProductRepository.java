package com.ecommerce.product.repository;

import com.ecommerce.product.model.Product;
import com.ecommerce.product.model.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Boolean existsByName(String name);

    Optional<Product> findByName(String name);

    Page<Product> findByProductType(ProductType productType, Pageable pageable);
}
