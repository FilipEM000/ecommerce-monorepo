package com.ecommerce.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "order_items")
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
    @ElementCollection
    private List<SelectedConfiguration> configurations;

    public BigDecimal getTotalItemPrice() {
        BigDecimal extraPrices = configurations.stream()
                .map(SelectedConfiguration::getAdditionalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return unitPrice.add(extraPrices).multiply(BigDecimal.valueOf(quantity));
    }
}
