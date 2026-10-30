package com.ecommerce.product.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ProductConfiguration {
    @Id
    @GeneratedValue
    private Long id;
    private String category;
    private String value;
    private Boolean isDefault = false;
    private BigDecimal additionalPrice;
}
