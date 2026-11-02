package com.ecommerce.order.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SelectedConfiguration {
    private String category;
    private String value;
    private BigDecimal additionalPrice;
}